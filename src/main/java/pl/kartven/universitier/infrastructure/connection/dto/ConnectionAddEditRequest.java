package pl.kartven.universitier.infrastructure.connection.dto;

import lombok.Data;

import java.util.Set;

@Data
public class ConnectionAddEditRequest {
    private final Long moduleId;
    private final Long academicYearId;
    private final Set<Long> groupsIds;
}
