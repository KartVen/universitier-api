package pl.kartven.universitier.infrastructure.module.dto;

import lombok.Value;

@Value
public class ModuleViewResponse {
    Long id;
    String name;
    Integer ects;
    Boolean isExam;
    Programme programme;

    @Value
    public static class Programme {
        Long id;
        String name;
    }
}
