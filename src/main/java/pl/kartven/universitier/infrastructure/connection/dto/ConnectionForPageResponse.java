package pl.kartven.universitier.infrastructure.connection.dto;

import lombok.Value;

import java.util.Set;

@Value
public class ConnectionForPageResponse {
    Long id;
    String moduleName;
    String academicYearRange;
    String groupName;
}
