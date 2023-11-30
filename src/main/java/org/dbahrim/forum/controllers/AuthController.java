package org.dbahrim.forum.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @PostMapping("/api/signup")
    @Operation(summary = "Sign up")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful, here's the JWT",
                    content = { @Content(mediaType = "application/text",
                            schema = @Schema(implementation = String.class)) })})
    public String signUp(@Valid @RequestBody UserRequest user) {
        return authenticationService.signUp(user);
    }

    @PostMapping("/api/login")
    @Operation(summary = "Sign in")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful, here's the JWT",
                    content = { @Content(mediaType = "application/text",
                            schema = @Schema(implementation = String.class)) }),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content)
            })
    public String logIn(@Valid @RequestBody UserRequest user) {
        return authenticationService.signIn(user);
    }

}
