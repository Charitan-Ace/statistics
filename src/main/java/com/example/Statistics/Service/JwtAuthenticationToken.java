package com.example.Statistics.Service;


import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;
    private String token;

    public JwtAuthenticationToken(String token) {
        super(null);
        this.token = token;
        this.principal = null; // Initially not authenticated
        setAuthenticated(false);
    }

    public JwtAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.token = null;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token; // Return the token as credentials
    }

    @Override
    public Object getPrincipal() {
        return principal; // Return the principal (e.g., username)
    }
}

