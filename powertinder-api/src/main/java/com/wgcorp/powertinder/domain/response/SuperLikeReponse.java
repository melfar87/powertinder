package com.wgcorp.powertinder.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wgcorp.powertinder.domain.entity.SuperLike;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SuperLikeReponse {

    @JsonProperty("match")
    private Boolean match;

    @JsonProperty("limit_exceeded")
    private Boolean limitExceeded;

    @JsonProperty("super_likes")
    private SuperLike superLikes;
}
