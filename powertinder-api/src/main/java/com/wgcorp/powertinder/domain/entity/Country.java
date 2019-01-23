package com.wgcorp.powertinder.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Country {

    @JsonProperty("name")
    private String name;

    @JsonProperty("cc")
    private String cc;
}
