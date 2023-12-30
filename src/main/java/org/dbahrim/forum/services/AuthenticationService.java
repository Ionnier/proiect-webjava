package org.dbahrim.forum.services;

import lombok.RequiredArgsConstructor;
import org.dbahrim.forum.configuration.SecurityConfig;
import org.dbahrim.forum.configuration.security.JwtService;
import org.dbahrim.forum.configuration.security.UserRequest;
import org.dbahrim.forum.data.UserRepository;
import org.dbahrim.forum.models.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public String signUp(UserRequest userRequest) {
        var user = new User(passwordEncoder.encode(userRequest.password), userRequest.email);
        user.setRole(SecurityConfig.ROLE_USER);
        userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        return jwt;
    }

    public String signIn(UserRequest userRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userRequest.getEmail(), userRequest.getPassword()));
        var user = userRepository.findByEmail(userRequest.getEmail());
        if (user == null) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        return jwtService.generateToken(user);
    }
}

