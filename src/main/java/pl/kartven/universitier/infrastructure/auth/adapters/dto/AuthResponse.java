package pl.kartven.universitier.infrastructure.auth.adapters.dto;

import lombok.Value;

@Value
public class AuthResponse {
    Long id;
    String bearer;
}
