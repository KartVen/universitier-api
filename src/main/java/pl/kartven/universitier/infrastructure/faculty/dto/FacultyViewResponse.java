package pl.kartven.universitier.infrastructure.faculty.dto;

import lombok.Value;

import java.util.Set;

@Value
public class FacultyViewResponse {
    Long id;
    String name;
    String shortName;
    String addressStreet;
    String addressZipCode;
    String addressCity;
    Integer yearFounded;
    Boolean isActive;
    Set<CourseDto> courses;

    @Value
    public static class CourseDto {
        Long id;
        String name;
    }
}
