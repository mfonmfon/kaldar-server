package com.kaldar.kaldar.shared.infrastructure.auth.service.impl;

import com.kaldar.kaldar.customer.domain.model.CustomerEntity;
import com.kaldar.kaldar.drycleaner.domain.model.DryCleanerEntity;
import com.kaldar.kaldar.shared.domain.constants.Role;
import com.kaldar.kaldar.shared.domain.model.UserEntity;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.request.AuthenticationRequest;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.AuthenticationResponse;
import com.kaldar.kaldar.shared.infrastructure.auth.dto.response.SessionResponse;
import com.kaldar.kaldar.shared.infrastructure.auth.service.AuthenticationService;
import com.kaldar.kaldar.shared.infrastructure.utility.CurrentUserResolver;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DefaultAuthenticationService implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CurrentUserResolver currentUserResolver;

    public DefaultAuthenticationService(AuthenticationManager authenticationManager,
                                       JwtService jwtService,
                                       CurrentUserResolver currentUserResolver) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.currentUserResolver = currentUserResolver;
    }

    @Override
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
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
                .collect(Collectors.toSet()));
        return authenticationResponse;
    }

    @Override
    public SessionResponse getSessionInfo() {
        UserEntity user = currentUserResolver.getCurrentUser();
        Set<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        Long customerId = null;
        Long dryCleanerId = null;
        String userType = "USER";

        if (user instanceof CustomerEntity || user.getRoles().contains(Role.CUSTOMER)) {
            customerId = user.getId();
            userType = "CUSTOMER";
        }
        if (user instanceof DryCleanerEntity || user.getRoles().contains(Role.DRY_CLEANER)) {
            dryCleanerId = user.getId();
            userType = "DRY_CLEANER";
        }

        return SessionResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(roles)
                .userType(userType)
                .customerId(customerId)
                .dryCleanerId(dryCleanerId)
                .build();
    }
}