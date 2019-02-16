package com.wgcorp.powertinder.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdateProfileRequest {

    @JsonProperty("age_filter_min")
    private Integer ageFilterMin;

    @JsonProperty("age_filter_max")
    private Integer ageFilterMax;

    @JsonProperty("gender_filter")
    private Integer genderFilter;

    @JsonProperty("gender")
    private Integer gender;

    @JsonProperty("distance_filter")
    private Integer distanceFilter;

    @JsonProperty("discoverable")
    private Boolean discoverable;
}
