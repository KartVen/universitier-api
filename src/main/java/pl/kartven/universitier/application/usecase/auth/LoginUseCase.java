package pl.kartven.universitier.application.usecase.auth;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.exception.BadRequestException;
import pl.kartven.universitier.infrastructure.auth.JwtTool;
import pl.kartven.universitier.infrastructure.auth.adapters.dto.AuthResponse;
import pl.kartven.universitier.infrastructure.auth.adapters.dto.LoginRequest;
import pl.kartven.universitier.infrastructure.auth.adapters.security.UserPrincipal;

import java.util.stream.Collectors;

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
                .mapLeft(ex -> Match(ex).of(
                        Case($(instanceOf(BadCredentialsException.class)),
                                e -> new BadRequestException("Invalid email or password")),
                        Case($(), e -> new BadRequestException(ex.getMessage()))
                ));
    }

    private AuthResponse authenticate(LoginRequest request) throws RuntimeException {
        SecurityContext context = SecurityContextHolder.getContext();
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()
                )
        );
        context.setAuthentication(auth);
        var principal = (UserPrincipal) context.getAuthentication().getPrincipal();
        var jwt = jwtTool.generateToken(map(principal));
        return new AuthResponse(principal.getId(), jwt.bearer());
    }

    private JwtTool.UserDetails map(UserPrincipal principal) {
        var authorities = principal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        return JwtTool.UserDetails.builder()
                .id(principal.getId())
                .username(principal.getUsername())
                .authorities(authorities)
                .build();
    }


}
