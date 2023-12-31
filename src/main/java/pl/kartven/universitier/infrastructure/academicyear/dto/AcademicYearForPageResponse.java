package pl.kartven.universitier.infrastructure.academicyear.dto;

import lombok.Value;

@Value
public class AcademicYearForPageResponse {
    Long id;
    Integer semesters;
    String range;
}
