package com.wgcorp.powertinder.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LikeResponse {

    @JsonProperty("match")
    private Object match;

    @JsonProperty("likes_remaining")
    private int likesRemaining;

    public Object getMatch() {
        return match;
    }

    public void setMatch(Object match) {
        this.match = match;
    }

    public int getLikesRemaining() {
        return likesRemaining;
    }

    public void setLikesRemaining(int likesRemaining) {
        this.likesRemaining = likesRemaining;
    }
}
