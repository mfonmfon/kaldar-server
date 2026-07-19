package com.kaldar.kaldar.shared.infrastructure.security;

import com.kaldar.kaldar.shared.domain.constants.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfiguration(AuthenticationProvider authenticationProvider,
                                 JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain doSecurityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                        //public auth route endpoint
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/customer/register", "/api/v1/drycleaner/register").permitAll()
                        // Public discovery endpoints
                        .requestMatchers("/api/v1/drycleaners/**").permitAll()
                        // Customer specific routes
                        .requestMatchers("/api/v1/customer/**").hasRole("CUSTOMER")

                        .requestMatchers(HttpMethod.POST, "/api/v1/order").hasRole("CUSTOMER")

                        .requestMatchers("/api/v1/order/review").hasRole("CUSTOMER")
                        // DryCleaner specific routes
                        .requestMatchers("/api/v1/drycleaner/**").hasRole("DRY_CLEANER")
                        .requestMatchers("/api/v1/order/accept", "/api/v1/order/reject", "/api/v1/order/status").hasRole("DRY_CLEANER")
                        // General order routes
                        .requestMatchers("/api/v1/order/**").authenticated()
                        .anyRequest().authenticated()
                );
            return http.build();
    }
}
