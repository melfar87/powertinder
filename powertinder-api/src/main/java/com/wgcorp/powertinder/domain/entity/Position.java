package com.wgcorp.powertinder.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Position {
    @JsonProperty("at")
    private Double at;

    @JsonProperty("lat")
    private Double lat;

    @JsonProperty("lon")
    private Double lon;

}
