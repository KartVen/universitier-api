package pl.kartven.universitier.infrastructure.course.dto;

import lombok.Value;

import java.util.Set;

@Value
public class CourseViewResponse {
    Long id;
    String name;
    Faculty faculty;
    Set<AcademicYear> academicYears;

    @Value
    public static class Faculty {
        Long id;
        String name;
    }

    @Value
    public static class AcademicYear {
        Long id;
        String academicYear;
    }

}
