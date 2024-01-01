package pl.kartven.universitier.application.usecase.auth;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.exception.BadRequestException;
import pl.kartven.universitier.application.exception.ForbiddenException;
import pl.kartven.universitier.application.exception.ServerProcessingException;
import pl.kartven.universitier.application.util.JwtHelper;
import pl.kartven.universitier.domain.model.Staff;
import pl.kartven.universitier.domain.model.User;
import pl.kartven.universitier.domain.repository.StaffRepository;
import pl.kartven.universitier.domain.repository.UserRepository;
import pl.kartven.universitier.infrastructure.auth.dto.AuthResponse;
import pl.kartven.universitier.infrastructure.auth.dto.RegistrationRequest;
import pl.kartven.universitier.infrastructure.auth.jwt.JwtTool;
import pl.kartven.universitier.infrastructure.auth.service.UserPrincipal;

import javax.servlet.http.Cookie;
import java.util.Set;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationUseCase {
    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTool jwtTool;
    private final RegistrationMapper mapper;

    @Value("${app.init:ONCE}")
    private InitMode initMode;

    public Either<ApiException, AuthResponse> execute(RegistrationRequest request) {
        if (initMode == InitMode.ONCE && userRepository.existsByRole(User.Role.ADMINISTRATOR))
            return Either.left(new ForbiddenException("Endpoint had been disabled"));

        var staff = mapper.map(request);
        staff.setUser(new User(passwordEncoder.encode(request.getPassword()), User.Role.ADMINISTRATOR));

        var staffEither = Try.of(() -> staffRepository.save(staff))
                .toEither()
                .mapLeft(th -> (ApiException) new ServerProcessingException(th.getMessage()));
        if (staffEither.isLeft()) return Either.left(staffEither.getLeft());

        return Try.of(() -> authenticate(request))
                .toEither()
                .mapLeft(ex -> Match(ex).<ApiException>of(
                        Case($(instanceOf(BadCredentialsException.class)),
                                e -> new BadRequestException("Invalid username/email or password")),
                        Case($(), e -> new BadRequestException(ex.getMessage()))
                ))
                .map(principal -> {
                    var jwt = jwtTool.generateToken(JwtHelper.map(principal));
                    return new AuthResponse(principal.getUsername(), jwt.bearer());
                });
    }

    private UserPrincipal authenticate(RegistrationRequest request) throws RuntimeException {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(), request.getPassword()
                )
        );
        return (UserPrincipal) auth.getPrincipal();
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

    @Mapper(componentModel = "spring")
    public interface RegistrationMapper {

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "user", ignore = true)
        @Mapping(target = "keyId", source = "username")
        @Mapping(target = "connections", ignore = true)
        @Mapping(target = "works", ignore = true)
        @Mapping(target = "addressStreet", source = "address.street")
        @Mapping(target = "addressHome", source = "address.home")
        @Mapping(target = "addressZipCode", source = "address.zipCode")
        @Mapping(target = "addressCity", source = "address.city")
        Staff map(RegistrationRequest request);
    }
}
