package com.wgcorp.powertinder.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SuperLikeRating {
    @JsonProperty("remaining")
    private int remaining;

    @JsonProperty("resets_at")
    private String resetAt;
}
