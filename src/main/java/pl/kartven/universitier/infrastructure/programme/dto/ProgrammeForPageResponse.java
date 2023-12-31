package pl.kartven.universitier.infrastructure.programme.dto;

import lombok.Value;

@Value
public class ProgrammeForPageResponse {
    String id;
    String name;
    String shortName;
    String courseName;
}
