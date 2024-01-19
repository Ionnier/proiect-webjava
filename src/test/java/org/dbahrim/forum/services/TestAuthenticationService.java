package org.dbahrim.forum.services;

import org.dbahrim.forum.configuration.security.JwtService;
import org.dbahrim.forum.configuration.security.UserRequest;
import org.dbahrim.forum.controllers.ErrorController;
import org.dbahrim.forum.data.UserRepository;
import org.dbahrim.forum.models.Event;
import org.dbahrim.forum.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestAuthenticationService {
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtService jwtService;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    UserRepository userRepository;

    @InjectMocks
    AuthenticationService authenticationService;

    @Mock
    Authentication authentication;

    @BeforeEach
    void setup() {
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken(any())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return ((UserDetails)invocation.getArguments()[0]).getPassword();
            }
        });
    }

    private final UserRequest validRequest = new UserRequest("asd@cti.ro", "kupokupokupo");

    @Test
    void testLogin() {
        when(userRepository.findByEmail(anyString())).thenReturn(new User("password", "email"));
        Assertions.assertEquals("password", authenticationService.signIn(validRequest));
    }

    @Test
    void testNoUserFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        Mockito.reset(jwtService);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.signIn(validRequest);
        });
        Assertions.assertEquals("Invalid email or password", exception.getMessage());
    }
}
