package com.japes.gateway.security;

import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>>{

	@Override
	public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
		String role = jwt.getClaimAsString("role");
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
		AbstractAuthenticationToken authentication = new JwtAuthenticationToken(jwt, List.of(authority));
		return Mono.just(authentication);
	}
}
