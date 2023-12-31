package pl.kartven.universitier.infrastructure.auth.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class RegistrationRequest {
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 8, message = "Username must be at least 8 characters long")
    private final String username;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private final String password;

    @NotBlank(message = "Firstname cannot be empty")
    private final String firstName;

    @NotBlank(message = "Lastname cannot be empty")
    private final String lastName;
}