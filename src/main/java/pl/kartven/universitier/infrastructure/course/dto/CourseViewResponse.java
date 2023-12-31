package pl.kartven.universitier.infrastructure.course.dto;

import lombok.Value;

import java.util.Set;

@Value
public class CourseViewResponse {
    Long id;
    String name;
    Faculty faculty;
    Set<Programme> programmes;

    @Value
    public static class Faculty {
        Long id;
        String name;
    }

    @Value
    public static class Programme {
        Long id;
        String name;
        String shortname;
    }
}
