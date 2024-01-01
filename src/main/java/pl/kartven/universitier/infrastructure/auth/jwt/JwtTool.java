package pl.kartven.universitier.infrastructure.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class JwtTool {
    @Value("${app.jwt.secret-key}")
    private String secretKey;
    @Value("${app.jwt.expiration-seconds:900}")
    private int expirationSeconds;

    public Jwt generateToken(UserDetails userDetails) {
        return generateToken(userDetails, expirationSeconds);
    }

    public Jwt generateToken(UserDetails userDetails, int expirationSeconds) {
        final var claims = Map.of(
                "id", userDetails.id,
                "username", userDetails.username,
                "authorities", userDetails.getAuthorities()
        );
        return createToken(userDetails.username, claims, expirationSeconds);
    }

    private Jwt createToken(String subject, Map<String, ?> claims, int expirationTime) {
        final Date createdDate = new Date(System.currentTimeMillis());
        final Date expirationDate = new Date(createdDate.getTime() + 1000L * expirationTime);
        String bearer = Jwts.builder()
                .setHeader(Map.of(
                        "alg", SignatureAlgorithm.HS512.getValue(),
                        Header.TYPE, Header.JWT_TYPE
                ))
                .setSubject(subject)
                .setClaims(claims)
                .setExpiration(expirationDate)
                .setIssuedAt(createdDate)
                .signWith(getSignKey(), SignatureAlgorithm.HS512)
                .compact();
        return new Jwt(bearer);
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenValid(String token) {
        try {
            return getClaimsJws(token).getBody()
                    .getExpiration()
                    .after(new Date(System.currentTimeMillis()));
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException | UnsupportedJwtException |
                 IllegalArgumentException e) {
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
                claims.get("authorities", List.class)
        );
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class UserDetails {
        private Long id;
        private String username;
        private Collection<String> authorities;
    }

    public record Jwt(String bearer) {
    }
}

