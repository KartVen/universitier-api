package pl.kartven.universitier.infrastructure.auth.adapters.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.kartven.universitier.domain.model.User;

import java.io.Serial;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
public class UserPrincipal implements UserDetails {
    @Serial
    private static final long serialVersionUID = 14237462382L;
    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;

    public static UserPrincipal map(User user) {
        return UserPrincipal.builder()
                .id(user.getId())
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(user.getRole().toString())))
                .isAccountNonExpired(!user.isAccountExpired())
                .isAccountNonLocked(!user.isAccountLocked())
                .isCredentialsNonExpired(!user.isCredentialsExpired())
                .isEnabled(user.isEnabled())
                .build();
    }
}
