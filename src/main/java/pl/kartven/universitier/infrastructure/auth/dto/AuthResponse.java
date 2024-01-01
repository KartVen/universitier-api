package pl.kartven.universitier.infrastructure.auth.dto;

import lombok.Value;

@Value
public class AuthResponse {
    String id;
    String token;
}
