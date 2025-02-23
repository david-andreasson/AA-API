package edu.campusmolndal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtConfig {
    // Hemlig nyckel för JWT-signering
    private String secret;

    // Token-giltighetsperiod i millisekunder
    private long expiration;
}