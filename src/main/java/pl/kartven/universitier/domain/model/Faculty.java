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
@Table(name = "faculties")
public class Faculty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @NotNull
    @Column(nullable = false)
    private String name;

    @NotBlank
    @NotNull
    @Column(nullable = false)
    private String shortname;

    private Integer yearFounded;

    @NotBlank
    @NotNull
    @Column(nullable = false)
    private String address;

    @OneToMany(mappedBy = "faculty")
    private Set<Course> courses = new HashSet<>();
}

