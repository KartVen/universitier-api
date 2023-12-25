package pl.kartven.universitier.infrastructure.programme.dto;

import lombok.Data;

import java.util.Set;

@Data
public class ProgrammeAddEditRequest {
    private final String name;
    private final Long courseId;
    private final Set<Long> academicYearsIds;
}
