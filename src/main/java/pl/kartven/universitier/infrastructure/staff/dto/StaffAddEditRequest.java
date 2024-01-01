package pl.kartven.universitier.infrastructure.staff.dto;

import lombok.Data;

import java.util.Set;

@Data
public class StaffAddEditRequest {
    private final String firstName;
    private final String lastName;
    private final String username;
    private final String password;
    private final RoleEDto role;
    private final AddressDto address;
    private final String email;
    private final String phone;
    private final Set<WorkDto> works;
    private final Set<GroupDto> groups;


    public enum RoleEDto {
        ACADEMIC_STAFF,
        NON_ACADEMIC_STAFF,
        LECTURER
    }

    @Data
    public static class AddressDto {
        private final String street;
        private final String home;
        private final String city;
        private final String zipCode;
    }

    @Data
    public static class WorkDto {
        private final String positionName;
        private final StatusEDto status;
        private final Long facultyId;

        public enum StatusEDto {
            PROGRESS,
            FINISHED
        }
    }

    @Data
    public static class GroupDto {
        private final Long moduleId;
        private final Long groupId;
        private final Long academicYearId;
    }
}
