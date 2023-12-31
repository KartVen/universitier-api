package pl.kartven.universitier.infrastructure.programme.dto;

import lombok.Value;

import java.util.Set;

@Value
public class ProgrammeViewResponse {
    Long id;
    String name;
    String shortName;
    Course course;
    Set<Module> modules;

    @Value
    public static class Course {
        Long id;
        String name;
    }

    @Value
    public static class Module {
        Long id;
        String name;
    }
}
