package pl.kartven.universitier.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "modules")
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @NotNull
    @Column(nullable = false)
    private String name;

    @NotBlank
    @NotNull
    @Size(max = 10)
    @Column(nullable = false)
    private String shortName;

    @NotNull
    @Column(nullable = false)
    @Min(0)
    @Max(12)
    private Integer ects;

    @NotNull
    @Column(nullable = false)
    @Min(0)
    private Integer hours;

    @NotNull
    @Column(nullable = false)
    private Boolean isExam;

    @NotNull
    @Column(nullable = false)
    private Boolean isActive;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "programme_id", nullable = false)
    private Programme programme;

    @OneToMany(mappedBy = "module", cascade = {
            CascadeType.MERGE, CascadeType.PERSIST,
    })
    private Set<Connection> connections = new HashSet<>();
}
