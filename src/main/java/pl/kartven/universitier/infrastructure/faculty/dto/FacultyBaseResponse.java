package pl.kartven.universitier.infrastructure.faculty.dto;

import lombok.Value;

@Value
public class FacultyBaseResponse {
    Long id;
    String name;
    String shortName;
}
