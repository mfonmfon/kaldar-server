package com.kaldar.kaldar.securitymodule.service.interfaces;

import com.kaldar.kaldar.dtos.request.AuthenticationRequest;
import com.kaldar.kaldar.dtos.response.AuthenticationResponse;

public interface AuthenticationService {

    AuthenticationResponse login(AuthenticationRequest authenticationRequest);
}
