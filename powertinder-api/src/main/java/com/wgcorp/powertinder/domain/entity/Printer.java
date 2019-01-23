package com.wgcorp.powertinder.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by gosse on 25/07/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Printer {
    private String name;

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("nom")
    public void setName(String name) {
        this.name = name;
    }
}
