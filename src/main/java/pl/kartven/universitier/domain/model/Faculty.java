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
    private String shortName;

    @NotNull
    @Column(nullable = false)
    private Integer yearFounded;

    @NotBlank
    @NotNull
    @Column(nullable = false)
    protected String addressStreet;

    @NotBlank
    @NotNull
    @Column(nullable = false)
    protected String addressZipCode;

    @NotBlank
    @NotNull
    @Column(nullable = false)
    protected String addressCity;

    @NotNull
    @Column(nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "faculty")
    private Set<Course> courses = new HashSet<>();
}

