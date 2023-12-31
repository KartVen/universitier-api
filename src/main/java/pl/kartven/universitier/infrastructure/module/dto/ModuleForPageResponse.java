package pl.kartven.universitier.infrastructure.module.dto;

import lombok.Value;

@Value
public class ModuleForPageResponse {
    Long id;
    String name;
    Integer ects;
    Boolean isExam;
    String programmeName;
}
