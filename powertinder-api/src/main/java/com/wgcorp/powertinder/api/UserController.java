package com.wgcorp.powertinder.api;

import com.wgcorp.powertinder.Constant;
import com.wgcorp.powertinder.domain.entity.Like;
import com.wgcorp.powertinder.domain.entity.MatchList;
import com.wgcorp.powertinder.domain.entity.Profile;
import com.wgcorp.powertinder.domain.entity.Recs;
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

        RequestEntity request = RequestEntity.get(uri)
                .header("X-Auth-Token", authService.xAuthToken())
                .header("Content-Type", MediaType.APPLICATION_JSON.toString())
                .header("User-Agent", Constant.USER_AGENT)
                .build();
        return restTemplate.exchange(request, Profile.class).getBody();
    }

    @ApiOperation(value = "Get next set of recommendations",
            response = Recs.class)
    @GetMapping("/user/recs")
    public Recs recos() throws IOException {
        URI uri = UriComponentsBuilder.fromUriString(baseUri).path("/user/recs").build().toUri();
        RequestEntity request = RequestEntity.get(uri)
                .header("X-Auth-Token", authService.xAuthToken())
                .header("Content-Type", MediaType.APPLICATION_JSON.toString())
                .header("User-Agent", Constant.USER_AGENT)
                .build();

        Recs recs = restTemplate.exchange(request, Recs.class).getBody();

        return recs;
    }

    @ApiOperation(value = "Get the list of all your matches",
            response = MatchList.class)
    @GetMapping("/user/matches")
    public MatchList matches() throws IOException {
        URI uri = UriComponentsBuilder.fromUriString(baseUri).path("/updates").build().toUri();

        FindMatchesRequest findMatchesRequest = new FindMatchesRequest();
        findMatchesRequest.setLastActivityDate("");

        RequestEntity<FindMatchesRequest> request = RequestEntity.post(uri)
                .header("X-Auth-Token", authService.xAuthToken())
                .header("Content-Type", MediaType.APPLICATION_JSON.toString())
                .header("User-Agent", Constant.USER_AGENT)
                .body(findMatchesRequest);

        return restTemplate.exchange(request, MatchList.class).getBody();
    }

    @ApiOperation(value = "Like someone",
            response = Like.class)
    @GetMapping("/user/like/{userId}")
    public Like like(@ApiParam(value = "unique id of the user you want to like", required = true) @PathVariable("userId") String userId) throws IOException {
        URI uri = UriComponentsBuilder.fromUriString(baseUri).path("/like/{userId}").buildAndExpand(userId).toUri();

        RequestEntity request = RequestEntity.get(uri)
                .header("X-Auth-Token", authService.xAuthToken())
                .header("Content-Type", MediaType.APPLICATION_JSON.toString())
                .header("User-Agent", Constant.USER_AGENT)
                .build();

        LikeResponse response = restTemplate.exchange(request, LikeResponse.class).getBody();

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
        URI uri = UriComponentsBuilder.fromUriString(baseUri).path("/like/{userId}/super").buildAndExpand(userId).toUri();

        RequestEntity request = RequestEntity.post(uri)
                .header("X-Auth-Token", authService.xAuthToken())
                .header("Content-Type", MediaType.APPLICATION_JSON.toString())
                .header("User-Agent", Constant.USER_AGENT)
                .build();

        return restTemplate.exchange(request, SuperLikeReponse.class).getBody();
    }

    @ApiOperation(value = "Pass someone",
            response = String.class)
    @GetMapping("/user/pass/{userId}")
    public ResponseEntity<String> pass(@PathVariable("userId") String userId) throws IOException {
        URI uri = UriComponentsBuilder.fromUriString(baseUri).path("/pass/{userId}").buildAndExpand(userId).toUri();

        RequestEntity request = RequestEntity.get(uri)
                .header("X-Auth-Token", authService.xAuthToken())
                .header("Content-Type", MediaType.APPLICATION_JSON.toString())
                .header("User-Agent", Constant.USER_AGENT)
                .build();

        return restTemplate.exchange(request, String.class);
    }

    @ApiOperation(value = "Update your current position by providing wanted latitude and longitude",
            response = String.class)
    @PostMapping("/user/position")
    public ResponseEntity<String> position(@RequestBody UpdatePositionRequest updatePositionRequest) throws IOException {
        URI uri = UriComponentsBuilder.fromUriString(baseUri).path("/user/ping").build().toUri();

        RequestEntity<UpdatePositionRequest> request = RequestEntity.post(uri)
                .header("X-Auth-Token", authService.xAuthToken())
                .header("Content-Type", MediaType.APPLICATION_JSON.toString())
                .header("User-Agent", Constant.USER_AGENT)
                .body(updatePositionRequest);

        return restTemplate.exchange(request, String.class);
    }
}
