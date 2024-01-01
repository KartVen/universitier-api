package pl.kartven.universitier.infrastructure.auth.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kartven.universitier.application.usecase.auth.ChangePasswordUseCase;
import pl.kartven.universitier.application.usecase.auth.LoginUseCase;
import pl.kartven.universitier.application.usecase.auth.RegistrationUseCase;
import pl.kartven.universitier.application.util.PredefinedApiResponse;
import pl.kartven.universitier.application.util.RestErrorHandler;
import pl.kartven.universitier.infrastructure.auth.dto.ChangePasswordRequest;
import pl.kartven.universitier.infrastructure.auth.dto.LoginRequest;
import pl.kartven.universitier.infrastructure.auth.dto.RegistrationRequest;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
@PredefinedApiResponse
public class AuthController implements RestErrorHandler {
    private final LoginUseCase loginUseCase;
    private final RegistrationUseCase registrationUseCase;
    private final ChangePasswordUseCase passwordUseCase;


    @PredefinedApiResponse.BadRequest
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request)  {
        return loginUseCase.execute(request).fold(this::handleError, ResponseEntity::ok);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
        return registrationUseCase.execute(request).fold(this::handleError, ResponseEntity::ok);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        return passwordUseCase.execute(request).fold(this::handleError, ResponseEntity::ok);
    }
}
