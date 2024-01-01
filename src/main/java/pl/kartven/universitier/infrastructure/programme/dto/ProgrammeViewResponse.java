package pl.kartven.universitier.infrastructure.programme.dto;

import lombok.Value;

import java.util.Set;

@Value
public class ProgrammeViewResponse {
    Long id;
    String name;
    String shortName;
    CourseDto course;
    Set<ModuleDto> modules;

    @Value
    public static class CourseDto {
        Long id;
        String name;
    }

    @Value
    public static class ModuleDto {
        Long id;
        String name;
    }
}
