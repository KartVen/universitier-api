package pl.kartven.universitier.infrastructure.academicyear.dto;

import lombok.Value;

@Value
public class AcademicYearBaseResponse {
    Long id;
    String mark;
    Boolean isClosed;
}
