package pl.kartven.universitier.infrastructure.module.dto;

import lombok.Value;

import java.util.Set;

@Value
public class ModuleForPageResponse {
    Long id;
    String name;
    Integer ects;
    Boolean isExam;
    String programmeName;
    Set<String> academicYearMarks;
}
