package com.wgcorp.powertinder.api;

import com.wgcorp.powertinder.Constant;
import com.wgcorp.powertinder.domain.entity.*;
import com.wgcorp.powertinder.domain.request.FindMatchesRequest;
import com.wgcorp.powertinder.domain.request.UpdatePositionRequest;
import com.wgcorp.powertinder.domain.response.LikeResponse;
import com.wgcorp.powertinder.domain.response.SuperLikeReponse;
import com.wgcorp.powertinder.security.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

@RestController
@CrossOrigin
@Api(value = "/user", description = "The tinder API proxy", produces = "application/json")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Value("${tinderapi.baseuri}")
    private String baseUri;

    @Autowired
    private AuthService authService;

    @Autowired
    private RestTemplate restTemplate;

    @ApiOperation(value = "Get information about the currently logged user (you)",
            response = Profile.class)
    @GetMapping("/user/me")
    public Profile me() throws IOException {
        LOGGER.debug("Calling /me endpoint");

        URI uri = UriComponentsBuilder.fromUriString(baseUri).path("/profile").build().toUri();
        return restTemplate.exchange(this.buildDefaultGetRequestEntity(uri), Profile.class).getBody();
    }

    @ApiOperation(value = "Get metadata information about the currently logged user (you)",
            response = Meta.class)
    @GetMapping("/user/meta")
    public Meta meta() throws IOException {
        LOGGER.debug("Calling /meta endpoint");

        URI uri = UriComponentsBuilder.fromUriString(baseUri).path("/meta").build().toUri();
        return restTemplate.exchange(this.buildDefaultGetRequestEntity(uri), Meta.class).getBody();
    }

    @ApiOperation(value = "Get next set of recommendations",
            response = Recs.class)
    @GetMapping(value = "/user/recs")
    public Recs recos() throws IOException {
        LOGGER.debug("Calling /recs endpoint");

        URI uri = UriComponentsBuilder.fromUriString(baseUri).path("/user/recs").build().toUri();
        return restTemplate.exchange(this.buildDefaultGetRequestEntity(uri), Recs.class).getBody();
    }

    @ApiOperation(value = "Get the list of all your matches with the ability to sort data by match date, name, age, distance",
            response = MatchList.class)
    @GetMapping(value = "/user/matches")
    public MatchList matches(@ApiParam(name="sort", value = "sort matches on the given attribute", allowableValues = "age, name, date, distance")
                                 @RequestParam(value = "sort", required = false) String sortCriteria) throws IOException {
        LOGGER.debug("Calling /matches endpoint");

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

    @ApiOperation(value = "Like someone",
            response = Like.class)
    @GetMapping("/user/like/{userId}")
    public Like like(@ApiParam(value = "unique id of the user you want to like", required = true) @PathVariable("userId") String userId) throws IOException {
        LOGGER.debug("Calling /like/{} endpoint", userId);

        URI uri = UriComponentsBuilder.fromUriString(baseUri).path("/like/{userId}").buildAndExpand(userId).toUri();
        LikeResponse response = restTemplate.exchange(this.buildDefaultGetRequestEntity(uri), LikeResponse.class).getBody();

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
        }

        return like;
    }

    @ApiOperation(value = "Super like someone",
            response = SuperLikeReponse.class)
    @GetMapping("/user/superlike/{userId}")
    public SuperLikeReponse superLike(@ApiParam(value = "unique id of the user you want to super like", required = true) @PathVariable("userId") String userId) throws IOException {
        LOGGER.debug("Calling /superlike/{} endpoint", userId);

        URI uri = UriComponentsBuilder.fromUriString(baseUri).path("/like/{userId}/super").buildAndExpand(userId).toUri();
        return restTemplate.exchange(this.buildDefaultPostRequestEntity(uri).build(), SuperLikeReponse.class).getBody();
    }

    @ApiOperation(value = "Pass someone",
            response = String.class)
    @GetMapping("/user/pass/{userId}")
    public ResponseEntity<String> pass(@PathVariable("userId") String userId) throws IOException {
        LOGGER.debug("Calling /pass/{} endpoint", userId);

        URI uri = UriComponentsBuilder.fromUriString(baseUri).path("/pass/{userId}").buildAndExpand(userId).toUri();
        return restTemplate.exchange(this.buildDefaultGetRequestEntity(uri), String.class);
    }

    @ApiOperation(value = "Update your current position by providing wanted latitude and longitude",
            response = String.class)
    @PostMapping("/user/position")
    public ResponseEntity<String> position(@RequestBody UpdatePositionRequest updatePositionRequest) throws IOException {
        LOGGER.debug("Calling /position endpoint with coordinates {}-{}", updatePositionRequest.getLat(), updatePositionRequest.getLon());

        URI uri = UriComponentsBuilder.fromUriString(baseUri).path("/user/ping").build().toUri();
        return restTemplate.exchange(this.buildDefaultPostRequestEntity(uri).body(updatePositionRequest), String.class);
    }

    private RequestEntity.BodyBuilder buildDefaultPostRequestEntity(URI uri) throws IOException {
        return RequestEntity.post(uri)
                .header("X-Auth-Token", authService.xAuthToken())
                .header("Content-Type", MediaType.APPLICATION_JSON.toString())
                .header("User-Agent", Constant.USER_AGENT);
    }

    private RequestEntity<Void> buildDefaultGetRequestEntity(URI uri) throws IOException {
        return RequestEntity.get(uri)
                .header("X-Auth-Token", authService.xAuthToken())
                .header("Content-Type", MediaType.APPLICATION_JSON.toString())
                .header("User-Agent", Constant.USER_AGENT)
                .build();
    }
}
