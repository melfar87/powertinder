package com.wgcorp.powertinder.service;

import com.wgcorp.powertinder.Constant;
import com.wgcorp.powertinder.domain.entity.*;
import com.wgcorp.powertinder.domain.request.FindMatchesRequest;
import com.wgcorp.powertinder.domain.request.UpdatePositionRequest;
import com.wgcorp.powertinder.domain.request.UpdateProfileRequest;
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
import java.util.List;
import java.util.Map;

// TODO need to refresh token on unauthorized
@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private static final int WAIT_TIME = 10000;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AuthService authService;

    @Autowired
    private TrilaterationService trilaterationService;

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

    @Cacheable(value = "user", condition = "#enableCaching")
    public Person user(String userId, boolean enableCaching) throws IOException {
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

        // Need to use header "Content" instead of "Content-Type"
        RequestEntity entity = RequestEntity.get(uri)
                .header("X-Auth-Token", authService.xAuthToken())
                .header("Content", MediaType.APPLICATION_JSON.toString())
                .build();

        return restTemplate.exchange(entity, String.class);
    }

    public ResponseEntity<String> position(UpdatePositionRequest updatePositionRequest) throws IOException {
        URI uri = UriComponentsBuilder.fromUriString(baseUri).path("/user/ping").build().toUri();
        return restTemplate.exchange(this.buildDefaultPostRequestEntity(uri).body(updatePositionRequest), String.class);
    }

    public ResponseEntity<String> updateProfile(UpdateProfileRequest updateProfileRequest) throws IOException {
        URI uri = UriComponentsBuilder.fromUriString(baseUri).path("/profile").build().toUri();
        return restTemplate.exchange(this.buildDefaultPostRequestEntity(uri).body(updateProfileRequest), String.class);
    }

    public Position locateUser(String userId) throws IOException, InterruptedException {
        // get my profile with my position
        Profile me = this.me();

        // get profile of user with distance
        Person userP1 = this.user(userId, false);
        Position p1 = me.getPos();
        p1.setAt(0.0);
        double d1 = userP1.getDistanceKm();

        // compute new points (NORTH, SOUTH, WEST, EAST)
        VirtualPosition vp1 = this.virtualPosition(userId, me.getPos(), userP1.getDistanceKm(), 0);
        VirtualPosition vp2 = this.virtualPosition(userId, me.getPos(), userP1.getDistanceKm() * 1.5, 90);
        VirtualPosition vp3 = this.virtualPosition(userId, me.getPos(), userP1.getDistanceKm(), 180);
        VirtualPosition vp4 = this.virtualPosition(userId, me.getPos(), userP1.getDistanceKm() * 1.5, 220);

        LOGGER.debug("Move back to initial position");
        UpdatePositionRequest updatePositionRequest = new UpdatePositionRequest();
        updatePositionRequest.setPosition(me.getPos());
        this.position(updatePositionRequest);

        // compute position
        double[] distances = {d1, vp1.distance, vp2.distance, vp3.distance, vp4.distance};
        return trilaterationService.approximateLocation(List.of(p1, vp1.pos, vp2.pos, vp3.pos, vp4.pos), distances);
    }

    private VirtualPosition virtualPosition(String userId, Position pos, double distance, int bearing) throws IOException, InterruptedException {
        // TODO check if position is not too far away from initial location, otherwise it will fail

        LOGGER.debug("Compute a new virtual location from {},{} / distance: {} km / bearing: {} degrees", pos.getLat(), pos.getLon(), distance, bearing);
        Position virtualPos = trilaterationService.destinationPoint(pos, distance * 1000, bearing);

        LOGGER.debug("Update position to virtual position {},{}", virtualPos.getLat(), virtualPos.getLon(), distance, bearing);
        UpdatePositionRequest updatePositionRequest = new UpdatePositionRequest();
        updatePositionRequest.setPosition(virtualPos);
        this.position(updatePositionRequest);

        LOGGER.debug("Has to wait some time for the target position to be updated... {} ms", WAIT_TIME);
        Thread.sleep(WAIT_TIME);

        LOGGER.debug("Get new distance from target with id {}", userId);
        double distanceFromTarget = this.user(userId, false).getDistanceKm();

        LOGGER.debug("New virtual position from target computed: {},{} at {}km", virtualPos.getLat(), virtualPos.getLon(), distanceFromTarget);
        return new VirtualPosition(virtualPos, distanceFromTarget);
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

    class VirtualPosition {
        Position pos;
        Double distance;

        public VirtualPosition(Position pos, Double distance) {
            this.pos = pos;
            this.distance = distance;
        }
    }
}
