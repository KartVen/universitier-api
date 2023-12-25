package pl.kartven.universitier.infrastructure.course.dto;

import lombok.Value;

@Value
public class CourseForPageResponse {
    String id;
    String name;
    String facultyShortName;
}
