package com.wgcorp.powertinder.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Profile {

    @JsonProperty("_id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("gender")
    private int gender;

    @JsonProperty("bio")
    private String bio;

    @JsonProperty("email")
    private String email;

    @JsonProperty("distance_filter")
    private int distanceFilter;

    @JsonProperty("ping_time")
    private String pingTime;

    @JsonProperty("discoverable")
    private Boolean discoverable;

    @JsonProperty("age_filter_max")
    private int ageFilterMax;

    @JsonProperty("age_filter_min")
    private int ageFilterMin;

    @JsonProperty("pos")
    private Position pos;

    @JsonProperty("pos_info")
    private PositionInfo posInfo;

    @JsonProperty("photos")
    private List<Photo> photos;

    @JsonProperty("api_token")
    private String apiToken;

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }
}
