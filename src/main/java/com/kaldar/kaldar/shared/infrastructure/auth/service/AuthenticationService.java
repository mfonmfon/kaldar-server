package com.kaldar.kaldar.shared.infrastructure.auth.service;

import com.kaldar.kaldar.shared.infrastructure.auth.dto.request.AuthenticationRequest;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.AuthenticationResponse;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.SessionResponse;

public interface AuthenticationService {

    AuthenticationResponse login(AuthenticationRequest authenticationRequest);

    SessionResponse getSessionInfo();
}
