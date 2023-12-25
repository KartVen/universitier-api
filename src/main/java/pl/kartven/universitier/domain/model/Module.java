package pl.kartven.universitier.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

    @NotNull
    @Column(nullable = false)
    @Min(0)
    @Max(12)
    Integer ects;

    @NotNull
    @Column(nullable = false)
    @Min(0)
    @Max(72)
    Integer hours;

    @NotNull
    @Column(nullable = false)
    Boolean isExam;

    @ManyToOne
    @NotNull
    @JoinColumn(name = "programme_id", nullable = false)
    private Programme programme;

    @ManyToMany
    @NotNull
    @JoinTable(name = "module_academic_years",
            joinColumns = @JoinColumn(name = "academic_year_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "module_id", nullable = false)
    )
    private Set<AcademicYear> academicYears = new HashSet<>();
}
