package com.wgcorp.powertinder.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Like {

    @JsonProperty("match")
    private Boolean match;

    @JsonProperty("created_date")
    private String createdDate;

    public Boolean getMatch() {
        return match;
    }

    public void setMatch(Boolean match) {
        this.match = match;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
