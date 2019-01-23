package com.wgcorp.powertinder.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SuperLike {

    @JsonProperty("remaining")
    private int remaining;

    @JsonProperty("allotment")
    private int allotment;

    @JsonProperty("superlike_refresh_amount")
    private int refreshAmout;

    @JsonProperty("superlike_refresh_interval")
    private int refreshInterval;

    @JsonProperty("superlike_refresh_interval_unit")
    private String refreshIntervalUnit;

    @JsonProperty("resets_at")
    private String resetsAt;

}
