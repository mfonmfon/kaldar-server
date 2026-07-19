package com.kaldar.kaldar.shared.infrastructure.auth.service.impl;

import com.kaldar.kaldar.shared.domain.model.UserEntity;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.request.AuthenticationRequest;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.AuthenticationResponse;
import com.kaldar.kaldar.shared.infrastructure.auth.service.AuthenticationService;
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
        authenticationResponse.setId(userEntity.getId());
        authenticationResponse.setEmail(userEntity.getEmail());
        authenticationResponse.setFirstName(userEntity.getFirstName());
        authenticationResponse.setLastName(userEntity.getLastName());
        authenticationResponse.setRoles(userEntity.getRoles().stream()
                .map(Enum::name)
                .collect(java.util.stream.Collectors.toSet()));
        return authenticationResponse;
    }


}