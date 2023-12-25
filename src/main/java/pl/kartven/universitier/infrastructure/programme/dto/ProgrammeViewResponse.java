package pl.kartven.universitier.infrastructure.programme.dto;

import lombok.Value;

import java.util.Set;

@Value
public class ProgrammeViewResponse {
    Long id;
    String name;
    Course course;
    Set<AcademicYear> academicYears;

    @Value
    public static class Course {
        Long id;
        String name;
    }

    @Value
    public static class AcademicYear {
        Long id;
        String academicYear;
    }

}
