package pl.kartven.universitier.infrastructure.connection.dto;

import lombok.Value;

import java.util.Set;

@Value
public class ConnectionViewResponse {
    Long id;
    Module module;
    AcademicYear academicYear;
    Set<Group> groups;

    @Value
    public static class Module {
        Long id;
        String name;
    }

    @Value
    public static class AcademicYear {
        Long id;
        String range;
    }

    @Value
    public static class Group {
        Long id;
        pl.kartven.universitier.domain.model.Group.Type type;
        Integer number;
    }
}
