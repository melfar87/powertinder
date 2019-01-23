package com.wgcorp.powertinder.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthRequest {

    @JsonProperty("facebook_token")
    private String facebookToken;

    @JsonProperty("facebook_id")
    private String facebookId;

    public String getFacebookToken() {
        return facebookToken;
    }

    public void setFacebookToken(String facebookToken) {
        this.facebookToken = facebookToken;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }
}
