package com.example.statistics.Config;

import com.example.statistics.service.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Bean
    public JwtService jwtService() {
        JwtService jwtService = new JwtService();
        jwtService.updatePublicKey( "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzhT6xB8TQRQBFK317gP0\n" +
                "PA3eVPSoqu+5BiLFD43vRZ79OF/wbrWu/ZoK+pprwvfU/qrwijapauT8QmSNU02i\n" +
                "RND9GkKirAS2jDx+X22R+w1IF928rNHaIIJQpGVf+1d/CvDMWXiRdGSSPi/JDdW1\n" +
                "vKm6mbOAXk7AizxDxqqbViO3SbvIvm10XGQCavlPOkpv3N8j3jbVi3bU0JkKEosq\n" +
                "2d3EZLxigGboEaL3dMdKzwt+p3UPZyfdK47I2Xal3tKzICtFi+FXnP5o00CE7bpq\n" +
                "B+xrkhvGf5gnYc9PNx9DE7bRRSIw5neGcoojuaB0bp9H+9pK0CB3NvH8BNImBq92\n" +
                "bwIDAQAB"); // Paste your public key
        return jwtService;
    }
}
