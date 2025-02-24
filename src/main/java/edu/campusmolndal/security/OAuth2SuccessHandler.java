package edu.campusmolndal.security;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.campusmolndal.service.JwtService;
import edu.campusmolndal.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final UserService userService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // Hämta OAuth2-användare från autentiseringsobjektet
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // Skapa eller uppdatera användare
        var user = userService.createOAuthUser(oAuth2User);

        // Generera JWT-token
        var token = jwtService.generateToken(user);

        // Förbered JSON-svar
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(Map.of(
                "token", token,
                "email", user.getEmail(),
                "name", user.getFirstName() + " " + user.getLastName()
        )));
    }
}