package edu.campusmolndal.controller;

import edu.campusmolndal.dto.AuthenticationRequest;
import edu.campusmolndal.dto.AuthenticationResponse;
import edu.campusmolndal.dto.RegisterRequest;
import edu.campusmolndal.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authService;

    // Endpoint för användarregistrering
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // Endpoint för inloggning
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    // Endpoint för OAuth2 lyckad inloggning
    @GetMapping("/oauth2/success")
    public ResponseEntity<Map<String, String>> handleOAuth2Success(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User != null) {
            Map<String, String> response = new HashMap<>();
            response.put("email", oauth2User.getAttribute("email"));
            response.put("name", oauth2User.getAttribute("name"));
            response.put("token", authService.handleOAuth2User(oauth2User));
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(Map.of("error", "No OAuth2 user found"));
    }

    // Endpoint för OAuth2 inloggningsfel
    @GetMapping("/oauth2/failure")
    public ResponseEntity<Map<String, String>> handleOAuth2Failure() {
        return ResponseEntity.badRequest().body(Map.of("error", "Authentication failed"));
    }
}