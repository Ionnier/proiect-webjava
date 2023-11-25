package org.dbahrim.forum.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dbahrim.forum.configuration.security.AuthenticationService;
import org.dbahrim.forum.configuration.security.JwtService;
import org.dbahrim.forum.configuration.security.UserRequest;
import org.dbahrim.forum.data.CategoryRepository;
import org.dbahrim.forum.data.UserRepository;
import org.dbahrim.forum.models.User;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@CrossOrigin(origins="*")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping
    @RequestMapping("/api/signup")
    public String signUp(@Valid @RequestBody UserRequest user) {
        return authenticationService.signUp(user);
    }

    @PostMapping
    @RequestMapping("/api/login")
    public String logIn(@Valid @RequestBody UserRequest user) {
        return authenticationService.signIn(user);
    }

}
