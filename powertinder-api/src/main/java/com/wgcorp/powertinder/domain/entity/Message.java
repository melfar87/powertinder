package com.wgcorp.powertinder.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {

    @JsonProperty("message")
    private String message;

    @JsonProperty("timestamp")
    private Double timestamp;
}
