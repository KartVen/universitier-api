package pl.kartven.universitier.infrastructure.student.dto;

import lombok.Data;
import pl.kartven.universitier.infrastructure.staff.dto.StaffAddEditRequest;

import java.util.Set;

@Data
public class StudentAddEditRequest {
    private final String firstName;
    private final String lastName;
    private final String password;
    private final Address address;
    private final String description;
    private final Set<Programme> programmes;

    @Data
    public static class Address {
        private final String street;
        private final String city;
        private final String zipCode;
        private final String postDist;
    }

    @Data
    public static class Programme {
        private final Long programmeId;
        private final Long academicYearId;
    }
}
