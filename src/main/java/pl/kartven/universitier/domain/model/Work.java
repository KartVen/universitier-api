package pl.kartven.universitier.domain.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "works")
public class Work {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @NotBlank
    @Column(nullable = false)
    private String positionName;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Nullable
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    public Work(Type type, String positionName, Status status, @Nullable Faculty faculty) {
        this.type = type;
        this.positionName = positionName;
        this.status = status;
        this.faculty = faculty;
    }

    public enum Type {
        ACADEMIC,
        NON_ACADEMIC,
    }

    public enum Status {
        PROGRESS,
        FINISHED,
    }
}