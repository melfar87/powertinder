package com.wgcorp.powertinder.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wgcorp.powertinder.domain.entity.Position;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LocateResponse {

    @JsonProperty
    private Position position;

    @JsonProperty
    private String mapsUrl;

    public LocateResponse(Position position) {
        this.position = position;
    }

    public String getMapsUrl() {
        return String.format("http://www.google.com/maps/place/%s,%s", position.getLat(), position.getLon());
    }
}
