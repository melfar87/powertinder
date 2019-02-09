package com.wgcorp.powertinder.api;

import com.wgcorp.powertinder.domain.entity.*;
import com.wgcorp.powertinder.domain.request.UpdatePositionRequest;
import com.wgcorp.powertinder.domain.response.SuperLikeReponse;
import com.wgcorp.powertinder.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@CrossOrigin
@Api(value = "/user", description = "The tinder API proxy", produces = "application/json")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @ApiOperation(value = "Get information about the currently logged user (you)",
            response = Profile.class)
    @GetMapping("/user/me")
    public Profile me() throws IOException {
        LOGGER.debug("Calling /me endpoint");
        return userService.me();
    }

    @ApiOperation(value = "Get metadata information about the currently logged user (you)",
            response = Meta.class)
    @GetMapping("/user/meta")
    public Meta meta() throws IOException {
        LOGGER.debug("Calling /meta endpoint");
        return userService.meta();
    }

    @ApiOperation(value = "Get detail about a user providing its id",
            response = Recs.class)
    @GetMapping("/user/{userId}")
    public Person user(@ApiParam(value = "unique id of the user you want to like", required = true) @PathVariable("userId") String userId) throws IOException {
        LOGGER.debug("Calling /user/{} endpoint", userId);
        return userService.user(userId);
    }

    @ApiOperation(value = "Get next set of recommendations", response = Recs.class)
    @GetMapping(value = "/user/recs")
    public Recs recos() throws IOException {
        LOGGER.debug("Calling /recs endpoint");
        return userService.recos();
    }

    @ApiOperation(value = "Get the list of all your matches with the ability to sort data by match date, name, age, distance",
            response = MatchList.class)
    @GetMapping(value = "/user/matches")
    public MatchList matches(@ApiParam(name = "sort", value = "sort matches on the given attribute", allowableValues = "age, name, date, distance")
                             @RequestParam(value = "sort", required = false) String sortCriteria) throws IOException {
        LOGGER.debug("Calling /matches endpoint");

        MatchList matchList = userService.matches(sortCriteria);

        // set the details for each match
        for (Match match : matchList.getMatches()) {
            match.setPerson(userService.user(match.getPerson().getId()));
        }

        return matchList;
    }

    // TODO no need to get meta for remaining like
    @ApiOperation(value = "Like someone",
            response = Like.class)
    @GetMapping("/user/like/{userId}")
    public Like like(@ApiParam(value = "unique id of the user you want to like", required = true) @PathVariable("userId") String userId) throws IOException {
        LOGGER.debug("Calling /like/{} endpoint", userId);
        return userService.like(userId);
    }

    @ApiOperation(value = "Super like someone",
            response = SuperLikeReponse.class)
    @GetMapping("/user/superlike/{userId}")
    public SuperLikeReponse superLike(@ApiParam(value = "unique id of the user you want to super like", required = true) @PathVariable("userId") String userId) throws IOException {
        LOGGER.debug("Calling /superlike/{} endpoint", userId);
        return userService.superLike(userId);
    }

    @ApiOperation(value = "Pass someone",
            response = String.class)
    @GetMapping("/user/pass/{userId}")
    public ResponseEntity<String> pass(@PathVariable("userId") String userId) throws IOException {
        LOGGER.debug("Calling /pass/{} endpoint", userId);
        return userService.pass(userId);
    }

    @ApiOperation(value = "Update your current position by providing wanted latitude and longitude",
            response = String.class)
    @PostMapping("/user/position")
    public ResponseEntity<String> position(@RequestBody UpdatePositionRequest updatePositionRequest) throws IOException {
        LOGGER.debug("Calling /position endpoint with coordinates {}-{}", updatePositionRequest.getLat(), updatePositionRequest.getLon());
        return userService.position(updatePositionRequest);
    }


}
