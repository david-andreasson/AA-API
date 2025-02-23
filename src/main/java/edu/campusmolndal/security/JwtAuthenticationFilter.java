package edu.campusmolndal.security;

import edu.campusmolndal.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Extrahera Authorization-header
        final String authHeader = request.getHeader("Authorization");

        // Hoppa över om ingen Authorization-header finns
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extrahera JWT-token
        final String jwt = authHeader.substring(7);
        final String userEmail = jwtService.extractUsername(jwt);

        // Kontrollera om användaren redan är autentiserad
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Hämta användardetaljer från databasen
            var userDetails = userDetailsService.loadUserByUsername(userEmail);

            // Validera token
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Skapa autentiseringsobjekt
                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Lägg till request-detaljer
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Sätt autentiseringskontext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Fortsätt filterkedjan
        filterChain.doFilter(request, response);
    }
}