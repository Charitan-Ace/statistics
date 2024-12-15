package com.example.Statistics.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private PublicKey publicKey;

    /**
     * Updates the current public key used for JWT validation.
     */
    public void updatePublicKey(String publicKeyStr) {
        try {
            // Decode the Base64-encoded public key
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyStr.replace("\n", "").trim());
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory kf = KeyFactory.getInstance("RSA"); // Ensure the algorithm matches your key type
            this.publicKey = kf.generatePublic(spec);
            System.out.println("Updated public key.");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Base64 public key format", e);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid public key format", e);
        }
    }


    /**
     * Validates a JWT using the current public key and extracts claims.
     */
    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public List<SimpleGrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = validateToken(token);
        List<String> roles = claims.get("roles", List.class); // Extract roles from claims
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // Add ROLE_ prefix
                .collect(Collectors.toList());
    }

}
