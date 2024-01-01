package pl.kartven.universitier.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @NotNull
    @Column(nullable = false)
    private String password;

    private boolean isActive;
    private boolean isExpired;
    private boolean isLocked;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    public User(String password, Role role) {
        this.password = password;
        this.role = role;
        this.isActive = true;
        this.isExpired = false;
        this.isLocked =  false;
    }

    public enum Role {
        ADMINISTRATOR,
        ACADEMIC_STAFF,
        NON_ACADEMIC_STAFF,
        LECTURER,
        STUDENT
    }
}
