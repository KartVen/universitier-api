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
    @Column(nullable = false)
    private Integer semesters;

    @NotNull
    @Column(name = "academic_year")
    @Size(max = 9)
    private String academicYear;

    @ManyToMany(mappedBy = "academicYears")
    @NotNull
    private Set<Course> courses = new HashSet<>();

    @ManyToMany(mappedBy = "academicYears")
    @NotNull
    private Set<Programme> programmes = new HashSet<>();

    @ManyToMany(mappedBy = "academicYears")
    @NotNull
    private Set<Module> modules = new HashSet<>();
}
