package pl.kartven.universitier.application.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import pl.kartven.universitier.infrastructure.auth.jwt.JwtTool;
import pl.kartven.universitier.infrastructure.auth.service.UserPrincipal;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtHelper {
    public static JwtTool.UserDetails map(UserPrincipal principal) {
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
