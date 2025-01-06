// com.example.statistics.Config.SecurityConfiguration.java
package com.example.statistics.Config;

import com.example.statistics.service.JwtService;
import com.example.statistics.service.RequestSignatureFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
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
                        .requestMatchers("/api/statistics/**").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new RequestSignatureFilter(jwtService),
                        UsernamePasswordAuthenticationFilter.class); // Add your custom filter

        return http.build();
    }
}
