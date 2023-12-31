package pl.kartven.universitier.infrastructure.programme.dto;

import lombok.Data;

@Data
public class ProgrammeAddEditRequest {
    private final String name;
    private final String shortName;
    private final Long courseId;
}
