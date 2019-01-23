package com.wgcorp.powertinder.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FindMatchesRequest {

    @JsonProperty("last_activity_date")
    private String lastActivityDate;

    public String getLastActivityDate() {
        return lastActivityDate;
    }

    public void setLastActivityDate(String lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }
}
