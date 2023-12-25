package pl.kartven.universitier.infrastructure.staff.dto;

import lombok.Value;

@Value
public class StaffForPageResponse {
    Long id;
    String firstName;
    String lastName;
    String email;
    boolean isActive;
}
