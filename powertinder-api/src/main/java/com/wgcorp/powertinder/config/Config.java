package com.wgcorp.powertinder.config;

import com.wgcorp.powertinder.security.AuthService;
import com.wgcorp.powertinder.security.AuthServiceFacebook;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!dev")
public class Config {

    @Bean
    public AuthService authService() {
        return new AuthServiceFacebook();
    }
}
