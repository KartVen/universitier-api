package pl.kartven.universitier.infrastructure.auth.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class LoginRequest {
    @NotBlank(message = "Username/email cannot be empty")
    @Pattern(
            regexp = "^[a-zA-Z]+(?:@universitier\\.edu\\.pl)?$",
            message = "Bad format of username/email"
    )
    private String usernameOrEmail;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
}