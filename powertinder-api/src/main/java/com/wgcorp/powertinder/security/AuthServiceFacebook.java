package com.wgcorp.powertinder.security;

import com.wgcorp.powertinder.domain.request.AuthRequest;
import com.wgcorp.powertinder.domain.response.ConnectedResponse;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wgcorp.powertinder.Constant.USER_AGENT;

@Service
@Profile("!dev")
public class AuthServiceFacebook implements AuthService{

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceFacebook.class);

    public static final int SLEEP_TIME = 1000;

    private String fbUrl = "https://www.facebook.com/v2.6/dialog/oauth?redirect_uri=fb464891386855067%3A%2F%2Fauthorize%2F&display=touch&state=%7B%22challenge%22%3A%22IUUkEUqIGud332lfu%252BMJhxL4Wlc%253D%22%2C%220_auth_logger_id%22%3A%2230F06532-A1B9-4B10-BB28-B29956C71AB1%22%2C%22com.facebook.sdk_client_state%22%3Atrue%2C%223_method%22%3A%22sfvc_auth%22%7D&scope=user_birthday%2Cuser_photos%2Cuser_education_history%2Cemail%2Cuser_relationship_details%2Cuser_friends%2Cuser_work_history%2Cuser_likes&response_type=token%2Csigned_request&default_audience=friends&return_scopes=true&auth_type=rerequest&client_id=464891386855067&ret=login&sdk=ios&logger_id=30F06532-A1B9-4B10-BB28-B29956C71AB1&ext=1470840777&hash=AeZqkIcf-NEW6vBd";

    @Value("${facebook.id}")
    private String facebookId;

    @Value("${facebook.email}")
    private String facebookEmail;

    @Value("${facebook.password}")
    private String facebookPassword;

    @Value("${tinderapi.baseuri}")
    private String baseUri;

    @Value("classpath:xhr.js")
    private Resource jsXhrInterceptor;

    @Autowired
    private RestTemplate restTemplate;

    private String facebookToken;

    @Value("${tinderapi.xAuthToken}")
    private String xAuthToken;

    @PostConstruct
    public void init() throws IOException {
        LOGGER.info("Init authentication service with following parameters : facebook.id='{}' - facebook.email='{}'", facebookId, facebookEmail);
        xAuthToken();
    }

    @Override
    public String xAuthToken() throws IOException {
        if (StringUtils.isEmpty(facebookId) || StringUtils.isEmpty(facebookEmail) || StringUtils.isEmpty(facebookPassword)) {
            LOGGER.error("No user information provided. Please fill in your user info in /config/application.yml file");
            return "";
        }

        if (StringUtils.isEmpty(xAuthToken)) {
            // get facebook token first
            if (StringUtils.isEmpty(facebookToken)) {
                facebookToken = this.requestFacebookToken();
            }

            // get xAuthToken
            AuthRequest authRequest = new AuthRequest();
            authRequest.setFacebookId(facebookId);
            authRequest.setFacebookToken(facebookToken);
            xAuthToken = this.requestXAuthToken(authRequest);

            LOGGER.info("Tinder API token : {}", xAuthToken);
        }

        return xAuthToken;
    }

    private String requestXAuthToken(AuthRequest authRequest) {
        URI uri = UriComponentsBuilder.fromUriString(baseUri).path("/auth").build().toUri();

        RequestEntity request = RequestEntity.post(uri)
                .header("Content-Type", MediaType.APPLICATION_JSON.toString())
                .header("User-Agent", USER_AGENT)
                .body(authRequest);

        ConnectedResponse connectedResponse = restTemplate.exchange(request, ConnectedResponse.class).getBody();
        return connectedResponse.getUser().getApiToken();
    }

    private String requestFacebookToken() throws IOException {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        WebDriver webDriver = null;
        String accessToken = "";

        try {
            webDriver = new ChromeDriver(chromeOptions);
            accessToken = scrapFacebookToken(webDriver);
        } catch (Exception e) {
            LOGGER.error("Unable to fetch facebook access_token", e);
        } finally {
            if (webDriver != null) {
                webDriver.close();
            }
        }

        return accessToken;
    }


    private String scrapFacebookToken(WebDriver webDriver) throws IOException, InterruptedException {
        LOGGER.info("Navigate to {}", fbUrl);
        webDriver.navigate().to(fbUrl);

        WebDriverWait wait = new WebDriverWait(webDriver, 10);

        WebElement email = webDriver.findElement(By.id("email"));
        WebElement password = webDriver.findElement(By.id("pass"));
        WebElement loginButton = webDriver.findElement(By.id("loginbutton"));

        LOGGER.info("Fill in facebook login form with credentials");
        email.sendKeys(facebookEmail);
        password.sendKeys(facebookPassword);

        LOGGER.info("Submit form");
        loginButton.click();

        JavascriptExecutor jsExecutor = (JavascriptExecutor) webDriver;

        LOGGER.info("Confirm authorization to Tinder app");
        WebElement confirmButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("__CONFIRM__")));

        // Need to add sleep between instructions... It fails otherwise not sure why
        Thread.sleep(SLEEP_TIME);

        LOGGER.info("Execute JS Script to be able to retrieve results from XHR requests");
        String externalJS = FileUtils.readFileToString(jsXhrInterceptor.getFile(), Charset.forName("UTF-8"));
        jsExecutor.executeScript(externalJS);

        Thread.sleep(SLEEP_TIME);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("interceptedResponse")));

        confirmButton.submit();

        Thread.sleep(SLEEP_TIME);

        LOGGER.info("Wait for interceptedResponse element to be present with its ready attribute set to true");
        wait.until(ExpectedConditions.attributeContains(By.id("interceptedResponse"), "ready", "true"));
        LOGGER.info("interceptedResponse element ready");

        String xhrResponse = webDriver.findElement(By.id("interceptedResponse")).getText();

        Pattern pattern = Pattern.compile("access_token=[a-zA-Z0-9]+");
        Matcher matcher = pattern.matcher(xhrResponse);

        String accessToken = "";
        if (matcher.find()) {
            accessToken = matcher.group().replace("access_token=", "");
            LOGGER.info("Access Token found : {}", accessToken);
        } else {
            LOGGER.error("Access Token not found");
        }

        return accessToken;
    }
}
