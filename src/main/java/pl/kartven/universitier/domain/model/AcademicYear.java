package pl.kartven.universitier.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "academic_years")
public class AcademicYear {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 9)
    @Column(nullable = false)
    private String mark;

    @NotNull
    @Column(nullable = false)
    private Boolean isClosed;

    @OneToMany(mappedBy = "academicYear")
    private Set<Connection> connections = new HashSet<>();
}
