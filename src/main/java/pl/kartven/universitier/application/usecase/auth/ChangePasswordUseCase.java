package pl.kartven.universitier.application.usecase.auth;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.exception.BadRequestException;
import pl.kartven.universitier.application.exception.ServerProcessingException;
import pl.kartven.universitier.application.util.JwtHelper;
import pl.kartven.universitier.domain.repository.UserRepository;
import pl.kartven.universitier.infrastructure.auth.dto.AuthResponse;
import pl.kartven.universitier.infrastructure.auth.dto.ChangePasswordRequest;
import pl.kartven.universitier.infrastructure.auth.jwt.JwtTool;
import pl.kartven.universitier.infrastructure.auth.service.UserPrincipal;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Component
@AllArgsConstructor
public class ChangePasswordUseCase {
    private final UserRepository userRepository;
    private AuthenticationManager authenticationManager;
    private JwtTool jwtTool;
    private final PasswordEncoder passwordEncoder;

    public Either<ApiException, AuthResponse> execute(ChangePasswordRequest request) {
        return Try.of(() -> authenticate(request))
                .toEither()
                .mapLeft(ex -> Match(ex).<ApiException>of(
                        Case($(instanceOf(BadCredentialsException.class)),
                                e -> new BadRequestException("Invalid username/email or password")),
                        Case($(), e -> new BadRequestException(ex.getMessage()))
                ))
                .flatMap(userPrincipal -> Option.ofOptional(userRepository.findById(userPrincipal.getId()))
                        .toEither((ApiException) new ServerProcessingException())
                        .map(user -> {
                            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                            return user;
                        })
                        .map(userRepository::save)
                        .map(user -> userPrincipal))
                .map(userPrincipal -> {
                    var jwt = jwtTool.generateToken(JwtHelper.map(userPrincipal));
                    return new AuthResponse(userPrincipal.getId().toString(), jwt.bearer());
                });
    }

    private UserPrincipal authenticate(ChangePasswordRequest request) throws RuntimeException {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(), request.getOldPassword()
                )
        );
        return (UserPrincipal) auth.getPrincipal();
    }
}
