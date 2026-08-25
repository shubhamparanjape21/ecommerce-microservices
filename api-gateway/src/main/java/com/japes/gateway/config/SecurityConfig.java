package com.japes.gateway.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import com.japes.gateway.security.JwtAuthenticationConverter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
	@Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {

        SecretKey secretKey = new SecretKeySpec(
                jwtSecret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        return NimbusReactiveJwtDecoder
                .withSecretKey(secretKey)
                .build();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            JwtAuthenticationConverter jwtAuthenticationConverter) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .authorizeExchange(exchange -> exchange

                        // Public endpoints
                        .pathMatchers(
                                "/api/v1/users/register",
                                "/api/v1/users/login",
                                "/actuator/health",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        
                        // Product - GET allowed for USER and ADMIN
                        .pathMatchers(HttpMethod.GET, "/api/v1/products/**").hasAnyRole("USER","ADMIN")
                        
                        // Product - ADMIN only
                        .pathMatchers(HttpMethod.POST, "/api/v1/products/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/v1/products/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/products/**").hasRole("ADMIN")
                        
                        // Category
                        .pathMatchers(HttpMethod.GET, "/api/v1/categories/**").hasAnyRole("USER","ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/v1/categories/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/v1/categories/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/categories/**").hasRole("ADMIN")
                        
                        // Product-Variants
                        .pathMatchers(HttpMethod.GET, "/api/v1/product-variants/**").hasAnyRole("USER","ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/v1/product-variants/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/v1/product-variants/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/product-variants/**").hasRole("ADMIN")

                        // Product-Variants
                        .pathMatchers("/api/v1/inventory/**").hasRole("ADMIN")
                        
                        // Order
                        .pathMatchers(HttpMethod.GET, "/api/v1/orders/**").hasAnyRole("USER","ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/v1/orders/**").hasAnyRole("USER","ADMIN")
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/orders/cancel/**").hasAnyRole("USER","ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/v1/orders/status/**").hasRole("ADMIN")
                        
                        // Everything else requires JWT authentication
                        .anyExchange()
                        .authenticated()
                )

                // JWT Bearer token authentication
                .oauth2ResourceServer(
                        oauth2 -> oauth2.jwt(jwt -> jwt
                        		.jwtAuthenticationConverter(
                        				jwtAuthenticationConverter
                        				)
                        		)
                )

                .build();
    }
}
