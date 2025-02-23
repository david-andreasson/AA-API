package edu.campusmolndal.service;

import edu.campusmolndal.dto.AuthenticationRequest;
import edu.campusmolndal.dto.AuthenticationResponse;
import edu.campusmolndal.dto.RegisterRequest;
import edu.campusmolndal.model.User;
import edu.campusmolndal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .provider("local")
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Transactional
    public String handleOAuth2User(OAuth2User oauth2User) {
        String email = oauth2User.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createOAuth2User(oauth2User));

        // Uppdatera användarinfo vid behov
        updateUserInfo(user, oauth2User);

        return jwtService.generateToken(user);
    }

    private User createOAuth2User(OAuth2User oauth2User) {
        User user = User.builder()
                .email(oauth2User.getAttribute("email"))
                .firstName(oauth2User.getAttribute("given_name"))
                .lastName(oauth2User.getAttribute("family_name"))
                .provider("google")
                .password(passwordEncoder.encode("")) // OAuth2-användare behöver inget lösenord
                .build();
        return userRepository.save(user);
    }

    private void updateUserInfo(User user, OAuth2User oauth2User) {
        user.setFirstName(oauth2User.getAttribute("given_name"));
        user.setLastName(oauth2User.getAttribute("family_name"));
        userRepository.save(user);
    }
}