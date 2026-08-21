package com.japes.userservice.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class UserSecurity {
	public boolean isOwnerOrAdmin(Long userId, Authentication authentication) {
        if (authentication == null ||
                !authentication.isAuthenticated()) {
            return false;
        }

        if (authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();

        return authenticatedUser.userId().equals(userId);
    }
}
