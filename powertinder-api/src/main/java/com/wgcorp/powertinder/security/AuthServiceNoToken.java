package com.wgcorp.powertinder.security;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Profile("dev")
public class AuthServiceNoToken implements AuthService {

    @Override
    public String xAuthToken() throws IOException {
        return "";
    }
}
