package pl.kartven.universitier.application.usecase.auth;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.exception.BadRequestException;
import pl.kartven.universitier.application.util.JwtHelper;
import pl.kartven.universitier.infrastructure.auth.jwt.JwtTool;
import pl.kartven.universitier.infrastructure.auth.dto.AuthResponse;
import pl.kartven.universitier.infrastructure.auth.dto.LoginRequest;
import pl.kartven.universitier.infrastructure.auth.service.UserPrincipal;

import javax.servlet.http.Cookie;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Service
@AllArgsConstructor
public class LoginUseCase {
    private AuthenticationManager authenticationManager;
    private JwtTool jwtTool;

    public Either<ApiException, AuthResponse> execute(LoginRequest request) {
        return Try.of(() -> authenticate(request))
                .toEither()
                .mapLeft(ex -> Match(ex).<ApiException>of(
                        Case($(instanceOf(BadCredentialsException.class)),
                                e -> new BadRequestException("Invalid username/email or password")),
                        Case($(), e -> new BadRequestException(ex.getMessage()))
                ))
                .map(principal -> {
                    var jwt = jwtTool.generateToken(JwtHelper.map(principal));
                    return new AuthResponse(principal.getId().toString(), jwt.bearer());
                });
    }

    private UserPrincipal authenticate(LoginRequest request) throws RuntimeException {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(), request.getPassword()
                )
        );
        return (UserPrincipal) auth.getPrincipal();
    }
}
