package pl.kartven.universitier.infrastructure.academicyear.dto;

import lombok.Data;

@Data
public class AcademicYearAddEditRequest {
    private final Boolean isClosed;
    private final String mark;
}
