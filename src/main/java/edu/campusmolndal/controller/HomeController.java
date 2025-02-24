package edu.campusmolndal.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.HashMap;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> home() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome! Login with Google at: /oauth2/authorization/google");
        response.put("login_url", "/oauth2/authorization/google");
        return response;
    }

    @GetMapping("/login")
    public Map<String, String> login() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Please log in using Google OAuth2");
        response.put("login_url", "/oauth2/authorization/google");
        return response;
    }

    @GetMapping("/dashboard")
    public Map<String, String> dashboard() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Welcome to your dashboard!");
        return response;
    }
}