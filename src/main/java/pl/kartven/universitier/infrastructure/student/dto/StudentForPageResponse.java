package pl.kartven.universitier.infrastructure.student.dto;

import lombok.Value;

@Value
public class StudentForPageResponse {
    Long id;
    String firstName;
    String lastName;
    Integer coursesCount;
    Boolean isActive;
}
