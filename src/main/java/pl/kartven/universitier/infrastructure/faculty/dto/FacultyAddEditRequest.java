package pl.kartven.universitier.infrastructure.faculty.dto;

import lombok.Data;

@Data
public class FacultyAddEditRequest {
    private final String name;
    private final String shortName;
    private final Integer yearFounded;
    private final AddressDto address;
    private final Boolean isActive;

    @Data
    public static class AddressDto {
        private final String street;
        private final String zipCode;
        private final String city;
    }

}
