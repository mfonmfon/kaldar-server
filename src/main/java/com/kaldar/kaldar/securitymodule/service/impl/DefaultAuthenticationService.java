package com.kaldar.kaldar.securitymodule.service.impl;

import com.kaldar.kaldar.usermdoule.UserEntity;
import com.kaldar.kaldar.dtos.request.AuthenticationRequest;
import com.kaldar.kaldar.dtos.response.AuthenticationResponse;
import com.kaldar.kaldar.kaldarService.implimentation.JwtService;
import com.kaldar.kaldar.securitymodule.service.interfaces.AuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class DefaultAuthenticationService implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public DefaultAuthenticationService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
      Authentication authentication =   authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authenticationRequest.getEmail(), authenticationRequest.getPassword()));
        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(userEntity);
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setAccessToken(jwtToken);
        return authenticationResponse;
    }
}