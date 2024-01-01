package pl.kartven.universitier.infrastructure.course.dto;

import lombok.Data;

import java.util.Set;

@Data
public class CourseAddEditRequest {
    private final String name;
    private final Long facultyId;
    private final Boolean isActive;
}
