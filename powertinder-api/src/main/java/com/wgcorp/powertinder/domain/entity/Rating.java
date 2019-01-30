package com.wgcorp.powertinder.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Rating {

    @JsonProperty("likes_remaining")
    private int likes_remaining;

    @JsonProperty("super_likes")
    private SuperLikeRating superLikeRating;
}

