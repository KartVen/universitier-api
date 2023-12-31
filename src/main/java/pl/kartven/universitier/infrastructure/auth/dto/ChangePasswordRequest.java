package pl.kartven.universitier.infrastructure.auth.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class ChangePasswordRequest {
    @NotBlank(message = "Username/email cannot be empty")
    @Pattern(
            regexp = "^[a-zA-Z]+(?:@universitier\\.edu\\.pl)?$",
            message = "Bad format of username/email"
    )
    private String usernameOrEmail;

    @NotBlank(message = "Old password cannot be empty")
    private String oldPassword;

    @NotBlank(message = "New password cannot be empty")
    @Size(min = 8, message = "New password must be at least 6 characters long")
    private String newPassword;
}