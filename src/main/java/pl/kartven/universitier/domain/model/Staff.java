package pl.kartven.universitier.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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
    @Size(max = 20)
    @Column(nullable = false, unique = true)
    private String keyId;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(cascade = {
            CascadeType.MERGE, CascadeType.PERSIST
    })
    @JoinTable(name = "staff_connections",
            joinColumns = @JoinColumn(name = "staff_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "connection_id", nullable = false)
    )
    private Set<Connection> connections = new HashSet<>();

    @OneToMany(cascade = {
            CascadeType.MERGE, CascadeType.PERSIST
    })
    @JoinColumn(name = "staff_id", nullable = false)
    private Set<Work> works = new HashSet<>();

    public Staff(String firstName, String lastName, String addressStreet, String addressHome, String addressZipCode, String addressCity, String email, String phone, String keyId, User user) {
        super(firstName, lastName, addressStreet, addressHome, addressZipCode, addressCity, email, phone);
        this.keyId = keyId;
        this.user = user;
    }
}
