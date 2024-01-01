package pl.kartven.universitier.infrastructure.student.dto;

import lombok.Data;

import java.util.Set;

@Data
public class StudentAddEditRequest {
    private final String firstName;
    private final String lastName;
    private final String password;
    private final AddressDto address;
    private final String email;
    private final String phone;
    private final Set<ProgrammeDto> programmes;

    @Data
    public static class AddressDto {
        private final String street;
        private final String home;
        private final String zipCode;
        private final String city;
    }

    @Data
    public static class ProgrammeDto {
        private final Long programmeId;
        private final Long academicYearId;
    }
}
