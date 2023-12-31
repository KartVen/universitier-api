package pl.kartven.universitier.infrastructure.module.dto;

import lombok.Data;

@Data
public class ModuleAddEditRequest {
    private final String name;
    private final Integer ects;
    private final Boolean isExam;
    private final Integer hours;
    private final Long programmeId;
}
