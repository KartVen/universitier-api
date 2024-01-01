package pl.kartven.universitier.infrastructure.course.dto;

import lombok.Value;

import java.util.Set;

@Value
public class CourseViewResponse {
    Long id;
    String name;
    FacultyDto faculty;
    Set<ProgrammeDto> programmes;

    @Value
    public static class FacultyDto {
        Long id;
        String name;
    }

    @Value
    public static class ProgrammeDto {
        Long id;
        String name;
        String shortName;
    }
}
