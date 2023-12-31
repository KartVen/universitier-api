package pl.kartven.universitier.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public abstract class Person {
    @NotBlank
    @NotNull
    @Column(nullable = false)
    protected String firstName;

    @NotBlank
    @NotNull
    @Column(nullable = false)
    protected String lastName;

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

    @NotBlank
    @NotNull
    @Column(nullable = false)
    protected String addressPostDist;
}