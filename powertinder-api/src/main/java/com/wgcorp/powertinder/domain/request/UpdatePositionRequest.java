package com.wgcorp.powertinder.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdatePositionRequest {
    @JsonProperty("lat")
    private float lat;

    @JsonProperty("lon")
    private float lon;
}
