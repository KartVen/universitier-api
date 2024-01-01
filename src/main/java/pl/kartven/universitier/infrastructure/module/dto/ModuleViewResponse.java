package pl.kartven.universitier.infrastructure.module.dto;

import lombok.Value;

import java.util.Set;

@Value
public class ModuleViewResponse {
    Long id;
    String name;
    String shortName;
    Integer ects;
    Boolean isExam;
    Boolean isActive;
    ProgrammeDto programme;
    Set<ConnectionDto> connections;

    @Value
    public static class ProgrammeDto {
        Long id;
        String name;
        String shortName;
    }

    @Value
    public static class ConnectionDto {
        Long id;
        AcademicYearDto academicYear;
        GroupDto group;

        @Value
        public static class ModuleDto {
            Long id;
            String name;
        }

        @Value
        public static class AcademicYearDto {
            Long id;
            String mark;
        }

        @Value
        public static class GroupDto {
            Long id;
            pl.kartven.universitier.domain.model.Group.Type type;
            Integer number;
        }
    }
}
