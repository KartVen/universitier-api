package pl.kartven.universitier.infrastructure.auth.jwt;

import io.vavr.control.Try;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.kartven.universitier.domain.repository.UserRepository;
import pl.kartven.universitier.infrastructure.auth.service.UserPrincipal;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtTool jwtTool;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        /*Try.of(() -> extractToken(request))
                .map(token -> Try.of(() -> token)
                        .filter(value -> value != null && jwtTool.isTokenValid(value))
                        .getOrNull())
                .map(jwtTool::deserializeToken)
                .onFailure(err -> log.warn("Failed to deserialize token"))
                .map(token -> userRepository.findByEmail(token.getUsername()).get())
                .map(user -> {
                    var userPrincipal = UserPrincipal.map(user, "");
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userPrincipal,
                            null,
                            userPrincipal.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    return true;
                })
                .onFailure(err -> log.warn("Could not set user authentication in security context"));
*/
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer "))
            return headerAuth.substring(7);
        return null;
    }
}
