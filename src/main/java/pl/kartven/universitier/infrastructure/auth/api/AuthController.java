package pl.kartven.universitier.infrastructure.auth.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.kartven.universitier.application.usecase.auth.LoginUseCase;
import pl.kartven.universitier.application.usecase.auth.RegistrationUseCase;
import pl.kartven.universitier.application.util.RestErrorHandler;
import pl.kartven.universitier.infrastructure.auth.adapters.dto.LoginRequest;
import pl.kartven.universitier.infrastructure.auth.adapters.dto.RegistrationRequest;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthController implements RestErrorHandler {
    private final LoginUseCase loginUseCase;
    private final RegistrationUseCase registrationUseCase;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request)  {
        return loginUseCase.execute(request).fold(this::handleError, ResponseEntity::ok);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
        return registrationUseCase.execute(request).fold(this::handleError, ResponseEntity::ok);
    }

    @GetMapping("/secure")
    @PreAuthorize("isAuthenticated")
    public String secureEndpoint() {
        return "This is a authenticated endpoint.";
    }

    @GetMapping("/public")
    public String publicEndpoint() {
        return "This is a public endpoint.";
    }
}
