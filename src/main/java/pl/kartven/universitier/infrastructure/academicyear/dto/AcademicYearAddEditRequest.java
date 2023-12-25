package pl.kartven.universitier.infrastructure.academicyear.dto;

import lombok.Data;

@Data
public class AcademicYearAddEditRequest {
    private final Integer semesters;
    private final String academicYear;
}
