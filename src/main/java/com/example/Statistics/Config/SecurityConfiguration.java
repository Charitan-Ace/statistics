package com.example.Statistics.Config;

import com.example.Statistics.Service.JwtService;
import com.example.Statistics.Service.RequestSignatureFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {

    private final JwtService jwtService;

    // Constructor injection for JwtService
    public SecurityConfiguration(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/projects/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new RequestSignatureFilter(jwtService),
                        UsernamePasswordAuthenticationFilter.class); // Add your custom filter

        return http.build();
    }
}
