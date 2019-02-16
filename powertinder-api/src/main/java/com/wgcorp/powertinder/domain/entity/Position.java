package com.wgcorp.powertinder.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Position {
    @JsonProperty("at")
    private Double at;

    @JsonProperty("lat")
    private Double lat;

    @JsonProperty("lon")
    private Double lon;

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getAt() {
        return at;
    }

    public void setAt(Double at) {
        this.at = at;
    }
}
