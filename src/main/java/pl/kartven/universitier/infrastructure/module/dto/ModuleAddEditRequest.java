package pl.kartven.universitier.infrastructure.module.dto;

import lombok.Data;

import java.util.Set;

@Data
public class ModuleAddEditRequest {
    private final String name;
    private final String shortName;
    private final Integer ects;
    private final Boolean isExam;
    private final Boolean isActive;
    private final Integer hours;
    private final Long programmeId;
    private final Set<ConnectionDto> connections;

    @Data
    public static class ConnectionDto {
        private final Long academicYearId;
        private final Long groupId;
    }
}
