package com.wgcorp.powertinder.security;

import java.io.IOException;

public class AuthServiceNoToken implements AuthService {

    @Override
    public String xAuthToken() throws IOException {
        return "";
    }
}
