package pl.kartven.universitier.infrastructure.faculty.dto;

import lombok.Value;

@Value
public class FacultyViewResponse {
    Long id;
    String name;
    String shortName;
    String address;
}
