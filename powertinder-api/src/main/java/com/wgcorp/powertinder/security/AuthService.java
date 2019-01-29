package com.wgcorp.powertinder.security;

import java.io.IOException;

public interface AuthService {

    String xAuthToken() throws IOException;
}
