package org.dbahrim.forum.configuration.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class UserRequest {
    @Email
    public final String email;
    @Size(min = 5, max = 20, message = "Password must have at least 5 characters and max 20 characters.")
    public final String password;
}
