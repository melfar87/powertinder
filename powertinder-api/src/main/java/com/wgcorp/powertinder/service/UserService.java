package com.wgcorp.powertinder.service;

import com.wgcorp.powertinder.Constant;
import com.wgcorp.powertinder.domain.entity.*;
import com.wgcorp.powertinder.domain.request.FindMatchesRequest;
import com.wgcorp.powertinder.domain.request.UpdatePositionRequest;
import com.wgcorp.powertinder.domain.response.DetailResponse;
import com.wgcorp.powertinder.domain.response.LikeResponse;
import com.wgcorp.powertinder.domain.response.SuperLikeReponse;
import com.wgcorp.powertinder.security.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Comparator;
import java.util.Map;

// TODO need to refresh token on unauthorized
@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AuthService authService;

    @Value("${tinderapi.baseuri}")
    private String baseUri;

    public Profile me() throws IOException {
        URI uri = UriComponentsBuilder.fromUriString(baseUri).path("/profile").build().toUri();
        return restTemplate.exchange(this.buildDefaultGetRequestEntity(uri), Profile.class).getBody();
    }

    public Meta meta() throws IOException {
        URI uri = UriComponentsBuilder.fromUriString(baseUri).path("/meta").build().toUri();
        return restTemplate.exchange(this.buildDefaultGetRequestEntity(uri), Meta.class).getBody();
    }

    @Cacheable("user")
    public Person user(String userId) throws IOException {
        LOGGER.debug("Get detail for user with id {}", userId);

        URI uri = UriComponentsBuilder.fromUriString(baseUri).path("/user/{userId}").buildAndExpand(userId).toUri();
        DetailResponse response = restTemplate.exchange(this.buildDefaultGetRequestEntity(uri), DetailResponse.class).getBody();

        return response.getPerson();
    }

    public Recs recos() throws IOException {
        URI uri = UriComponentsBuilder.fromUriString(baseUri).path("/user/recs").build().toUri();
        return restTemplate.exchange(this.buildDefaultGetRequestEntity(uri), Recs.class).getBody();
    }

    public MatchList matches(String sortCriteria) throws IOException {
        URI uri = UriComponentsBuilder.fromUriString(baseUri).path("/updates").build().toUri();

        FindMatchesRequest findMatchesRequest = new FindMatchesRequest();
        findMatchesRequest.setLastActivityDate("");

        MatchList matchList = restTemplate.exchange(this.buildDefaultPostRequestEntity(uri).body(findMatchesRequest), MatchList.class).getBody();

        // sorting depending on sort parameter
        if (sortCriteria != null) {
            switch (sortCriteria) {
                case "age":
                    matchList.getMatches().sort(Comparator.comparing(match -> match.getPerson().getAge()));
                    break;
                case "name":
                    matchList.getMatches().sort(Comparator.comparing(match -> match.getPerson().getName()));
                    break;
                case "distance":
                    matchList.getMatches().sort(Comparator.comparing(match -> match.getPerson().getDistanceMi()));
                    break;
                case "date":
                    matchList.getMatches().sort(Comparator.comparing(match -> match.getCreatedDate()));
                    break;
            }
        }

        return matchList;
    }

    public Like like(String userId) throws IOException {
        URI uri = UriComponentsBuilder.fromUriString(baseUri).path("/like/{userId}").buildAndExpand(userId).toUri();

        // Need to use header "Content" instead of "Content-Type"
        RequestEntity entity = RequestEntity.get(uri)
                .header("X-Auth-Token", authService.xAuthToken())
                .header("Content", MediaType.APPLICATION_JSON.toString())
                .build();

        LikeResponse response = restTemplate.exchange(entity, LikeResponse.class).getBody();

        // TODO write custom serializer here instead
        Like like = new Like();
        if (response != null) {
            if (response.getMatch() instanceof Boolean) {
                like.setMatch(false);
            } else {
                Map matchElement = (Map) response.getMatch();
                like.setMatch(true);
                like.setCreatedDate((String) matchElement.get("created_date"));
            }
            like.setLikesRemaining(response.getLikesRemaining());
        }

        return like;
    }

    public SuperLikeReponse superLike(String userId) throws IOException {
        URI uri = UriComponentsBuilder.fromUriString(baseUri).path("/like/{userId}/super").buildAndExpand(userId).toUri();
        return restTemplate.exchange(this.buildDefaultPostRequestEntity(uri).build(), SuperLikeReponse.class).getBody();
    }

    public ResponseEntity<String> pass(String userId) throws IOException {
        URI uri = UriComponentsBuilder.fromUriString(baseUri).path("/pass/{userId}").buildAndExpand(userId).toUri();
        return restTemplate.exchange(this.buildDefaultGetRequestEntity(uri), String.class);
    }

    public ResponseEntity<String> position(UpdatePositionRequest updatePositionRequest) throws IOException {
        URI uri = UriComponentsBuilder.fromUriString(baseUri).path("/user/ping").build().toUri();
        return restTemplate.exchange(this.buildDefaultPostRequestEntity(uri).body(updatePositionRequest), String.class);
    }

    private RequestEntity.BodyBuilder buildDefaultPostRequestEntity(URI uri) throws IOException {
        return RequestEntity.post(uri)
                .header("X-Auth-Token", authService.xAuthToken())
                .header("Content-Type", MediaType.APPLICATION_JSON.toString())
                .header("User-Agent", Constant.USER_AGENT);
    }

    private RequestEntity buildDefaultGetRequestEntity(URI uri) throws IOException {
        return RequestEntity.get(uri)
                .header("X-Auth-Token", authService.xAuthToken())
                .header("Content-Type", MediaType.APPLICATION_JSON.toString())
                .build();
    }
}
