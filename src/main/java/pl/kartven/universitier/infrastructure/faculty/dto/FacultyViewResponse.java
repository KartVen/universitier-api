package pl.kartven.universitier.infrastructure.faculty.dto;

import lombok.Value;

import java.util.Set;

@Value
public class FacultyViewResponse {
    Long id;
    String name;
    String shortName;
    Integer yearFounded;
    String address;
    Set<Course> courses;

    @Value
    public static class Course {
        Long id;
        String name;
    }
}
