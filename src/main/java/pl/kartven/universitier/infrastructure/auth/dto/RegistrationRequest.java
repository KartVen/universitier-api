package pl.kartven.universitier.infrastructure.auth.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class RegistrationRequest {
    @NotBlank
    @Size(min = 8)
    private final String username;
    @NotBlank
    @Size(min = 8)
    private final String password;
    @NotBlank
    private final String firstName;
    @NotBlank
    private final String lastName;
    @NotBlank
    private final AddressDto address;
    @NotBlank
    @Email
    private final String email;
    @NotBlank
    private final String phone;

    @Data
    public static class AddressDto {
        @NotBlank
        private final String street;
        @NotBlank
        private final String home;
        @NotBlank
        private final String city;
        @NotBlank
        private final String zipCode;
    }
}