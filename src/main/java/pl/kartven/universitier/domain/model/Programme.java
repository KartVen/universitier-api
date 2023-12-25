package pl.kartven.universitier.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "programmes")
public class Programme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @NotNull
    @Column(nullable = false)
    private String name;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToMany(mappedBy = "programme")
    private Set<Module> modules = new HashSet<>();

    @ManyToMany
    @NotNull
    @JoinTable(name = "programme_academic_years",
            joinColumns = @JoinColumn(name = "programme_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "academic_year_id", nullable = false)
    )
    private Set<AcademicYear> academicYears = new HashSet<>();
}
