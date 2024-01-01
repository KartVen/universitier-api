package pl.kartven.universitier.infrastructure.academicyear.dto;

import lombok.Value;

@Value
public class AcademicYearViewResponse {
    Long id;
    String mark;
    Boolean isClosed;
}
