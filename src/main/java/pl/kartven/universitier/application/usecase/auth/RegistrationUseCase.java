package pl.kartven.universitier.application.usecase.auth;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.exception.BadRequestException;
import pl.kartven.universitier.application.exception.ServerProcessingException;
import pl.kartven.universitier.domain.model.User;
import pl.kartven.universitier.domain.repository.UserRepository;
import pl.kartven.universitier.infrastructure.auth.JwtTool;
import pl.kartven.universitier.infrastructure.auth.adapters.dto.AuthResponse;
import pl.kartven.universitier.infrastructure.auth.adapters.dto.RegistrationRequest;

import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class RegistrationUseCase {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTool jwtTool;

    public Either<ApiException, AuthResponse> execute(RegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return Either.left(new BadRequestException("Email is already in use"));
        }
        var user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                User.Role.GUEST
        );
        return Try.of(() -> userRepository.save(user))
                .toEither()
                .mapLeft(th -> (ApiException) new ServerProcessingException(th.getMessage()))
                .map(userEntity -> {
                    var jwt = jwtTool.generateToken(map(userEntity));
                    return new AuthResponse(userEntity.getId(), jwt.bearer());
                });
    }

    private JwtTool.UserDetails map(User user) {
        return JwtTool.UserDetails.builder()
                .id(user.getId())
                .username(user.getEmail())
                .authorities(Set.of(user.getRole().name()))
                .build();
    }
}
