package pl.kartven.universitier.infrastructure.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@Component
public class JwtTool {
    @Value("${app.jwt.secret-key}")
    private String secretKey;
    @Value("${app.jwt.expiration-time:900000}")
    private int expirationTime;

    public Jwt generateToken(UserDetails userDetails) {
        return generateToken(userDetails, expirationTime);
    }

    public Jwt generateToken(UserDetails userDetails, int expirationTime) {
        final var claims = Map.of(
                "id", userDetails.id,
                "username", userDetails.username,
                "authorities", userDetails.getAuthorities()
        );
        return createToken(userDetails.username, claims, expirationTime);
    }

    private Jwt createToken(String subject, Map<String, ?> claims, int expirationTime) {
        final Date createdDate = new Date(System.currentTimeMillis());
        final Date expirationDate = new Date(createdDate.getTime() + expirationTime);
        String bearer = Jwts.builder()
                .setSubject(subject)
                .setClaims(claims)
                .setExpiration(expirationDate)
                .setIssuedAt(createdDate)
                .signWith(getSignKey(), SignatureAlgorithm.HS512)
                .compact();
        return new Jwt(bearer);
    }

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public boolean isTokenValid(String token) {
        try {
            return !getClaimsJws(token).getBody()
                    .getExpiration()
                    .before(new Date(System.currentTimeMillis()));
        } catch (Exception e) {
            return false;
        }
    }

    private Jws<Claims> getClaimsJws(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token);
    }

    @SuppressWarnings("unchecked")
    public UserDetails deserializeToken(String token) {
        Claims claims = getClaimsJws(token).getBody();
        return new UserDetails(
                claims.get("id", Long.class),
                claims.get("username", String.class),
                claims.get("authorities", Set.class)
        );
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class UserDetails {
        private Long id;
        private String username;
        private Set<String> authorities;
    }

    public record Jwt(String bearer) {
    }
}

