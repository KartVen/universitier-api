package pl.kartven.universitier.infrastructure.faculty.dto;

import lombok.Data;

@Data
public class FacultyAddEditRequest {
    private final Long id;
    private final String name;
    private final String shortName;
    private final String address;
}
