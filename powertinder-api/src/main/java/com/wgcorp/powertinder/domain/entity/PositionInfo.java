package com.wgcorp.powertinder.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PositionInfo {

    @JsonProperty("city")
    private City city;

    @JsonProperty("country")
    private Country country;
}
