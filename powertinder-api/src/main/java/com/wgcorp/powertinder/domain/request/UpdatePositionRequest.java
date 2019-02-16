package com.wgcorp.powertinder.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wgcorp.powertinder.domain.entity.Position;

public class UpdatePositionRequest {

    @JsonProperty("lat")
    private double lat;

    @JsonProperty("lon")
    private double lon;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public void setPosition(Position p) {
        this.lat = p.getLat();
        this.lon = p.getLon();
    }
}
