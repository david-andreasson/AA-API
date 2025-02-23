package edu.campusmolndal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;

// Konfigurationsklass för att undvika cirkulära beroenden
@Configuration
public class ApplicationConfig {
    // Konfigurera lösenordshantering med BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Konfigurera ObjectMapper för JSON-hantering
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}