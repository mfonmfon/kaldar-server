package com.kaldar.kaldar.shared.infrastructure.security;

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

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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
    public SecurityFilterChain doSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(
                        authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                                // public auth route endpoint
                                .requestMatchers("/api/v1/auth/**").permitAll()
                                .requestMatchers("/api/v1/customer/register", "/api/v1/drycleaner/register").permitAll()
                                // Swagger / OpenAPI docs
                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                                // Public discovery endpoints
                                .requestMatchers("/api/v1/drycleaners/**").permitAll()
                                // Customer specific routes
                                .requestMatchers("/api/v1/customer/**").hasRole("CUSTOMER")

                                .requestMatchers(HttpMethod.POST, "/api/v1/order").hasRole("CUSTOMER")

                                .requestMatchers("/api/v1/order/review").hasRole("CUSTOMER")
                                // DryCleaner specific routes
                                .requestMatchers("/api/v1/drycleaner/**").hasRole("DRY_CLEANER")
                                .requestMatchers("/api/v1/order/accept", "/api/v1/order/reject", "/api/v1/order/status")
                                .hasRole("DRY_CLEANER")
                                // General order routes
                                .requestMatchers("/api/v1/order/**").authenticated()
                                // Notification routes (any authenticated user)
                                .requestMatchers("/api/v1/notifications/**").authenticated()
                                // Favourite routes (customers only)
                                .requestMatchers("/api/v1/favorites/**").hasRole("CUSTOMER")
                                // Wallet customer-facing balance endpoint
                                .requestMatchers("/api/v1/wallet/**").hasRole("CUSTOMER")
                                // Payment: webhook is public (signature verified in service), rest is CUSTOMER
                                .requestMatchers("/api/v1/payment/webhook").permitAll()
                                .requestMatchers("/api/v1/payment/**").hasRole("CUSTOMER")
                                .anyRequest().authenticated());
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "x-paystack-signature"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
