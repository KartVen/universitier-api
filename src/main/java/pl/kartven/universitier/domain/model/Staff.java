package pl.kartven.universitier.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "staffs")
public class Staff extends Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @NotNull
    @Column(nullable = false, unique = true)
    @Size(max = 20)
    private String referenceId;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(name = "staff_connections",
            joinColumns = @JoinColumn(name = "staff_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "connection_id", nullable = false)
    )
    private Set<Connection> connections = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "staff_id", nullable = false)
    private Set<Work> works = new HashSet<>();

    public Staff(String referenceId, String firstName, String lastName, User user) {
        this.referenceId = referenceId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.user = user;
    }
}
