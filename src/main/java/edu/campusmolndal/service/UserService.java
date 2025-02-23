package edu.campusmolndal.service;

import edu.campusmolndal.model.User;
import edu.campusmolndal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Användare inte hittad med email: " + email));
    }

    @Transactional
    public User createOAuthUser(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");

        return userRepository.findByEmail(email)
                .map(user -> updateExistingUser(user, oAuth2User))
                .orElseGet(() -> createNewUser(oAuth2User));
    }

    private User updateExistingUser(User user, OAuth2User oAuth2User) {
        user.setFirstName(oAuth2User.getAttribute("given_name"));
        user.setLastName(oAuth2User.getAttribute("family_name"));
        return userRepository.save(user);
    }

    private User createNewUser(OAuth2User oAuth2User) {
        return userRepository.save(User.builder()
                .email(oAuth2User.getAttribute("email"))
                .firstName(oAuth2User.getAttribute("given_name"))
                .lastName(oAuth2User.getAttribute("family_name"))
                .password(passwordEncoder.encode(""))
                .provider("google")
                .build());
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Användare inte hittad"));
    }

    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}