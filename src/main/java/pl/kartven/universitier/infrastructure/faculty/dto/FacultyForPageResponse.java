package pl.kartven.universitier.infrastructure.faculty.dto;

import lombok.Value;

@Value
public class FacultyForPageResponse {
    Long id;
    String name;
    String shortName;
    Integer yearFounded;
}
