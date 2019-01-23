package com.wgcorp.powertinder.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wgcorp.powertinder.domain.entity.Profile;

public class ConnectedResponse {

    @JsonProperty("user")
    private Profile user;

    public Profile getUser() {
        return user;
    }

    public void setUser(Profile user) {
        this.user = user;
    }
}
