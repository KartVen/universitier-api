package pl.kartven.universitier.application.usecase.auth;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.exception.ForbiddenException;
import pl.kartven.universitier.application.exception.ServerProcessingException;
import pl.kartven.universitier.domain.model.Staff;
import pl.kartven.universitier.domain.model.User;
import pl.kartven.universitier.domain.repository.StaffRepository;
import pl.kartven.universitier.domain.repository.UserRepository;
import pl.kartven.universitier.infrastructure.auth.jwt.JwtTool;
import pl.kartven.universitier.infrastructure.auth.dto.AuthResponse;
import pl.kartven.universitier.infrastructure.auth.dto.RegistrationRequest;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationUseCase {
    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTool jwtTool;

    @Value("${app.init:ONCE}")
    private InitMode initMode;

    public Either<ApiException, AuthResponse> execute(RegistrationRequest request) {
        if (initMode == InitMode.ONCE && userRepository.existsByRole(User.Role.ADMINISTRATOR))
            return Either.left(new ForbiddenException("Endpoint had been disabled"));

        var user = new User(passwordEncoder.encode(request.getPassword()), User.Role.ADMINISTRATOR);
        var staff = new Staff(
                request.getUsername(), request.getFirstName(), request.getLastName(), user
        );
        var staffEither = Try.of(() -> staffRepository.save(staff))
                .toEither()
                .mapLeft(th -> (ApiException) new ServerProcessingException(th.getMessage()));
        if (staffEither.isLeft()) return Either.left(staffEither.getLeft());

        return Option.ofOptional(staffRepository.findByRefIdWithU(request.getUsername()))
                .map(staffEnt -> {
                    var jwt = jwtTool.generateToken(map(staffEnt.getUser(), staffEnt.getReferenceId()));
                    return new AuthResponse(staffEnt.getReferenceId(), jwt.bearer());
                })
                .toEither(new ServerProcessingException());
    }

    private JwtTool.UserDetails map(User user, String username) {
        return JwtTool.UserDetails.builder()
                .id(user.getId())
                .username(username)
                .authorities(Set.of(user.getRole().name()))
                .build();
    }

    public enum InitMode {
        ONCE,
        MORE;
    }
}
