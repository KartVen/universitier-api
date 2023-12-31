package pl.kartven.universitier.infrastructure.staff.dto;

import lombok.Data;
import pl.kartven.universitier.domain.model.User;
import pl.kartven.universitier.domain.model.Work;

import java.util.Set;

@Data
public class StaffAddEditRequest {
    private final String firstName;
    private final String lastName;
    private final String username;
    private final String password;
    private final User.Role role;
    private final Address address;
    private final Set<Work> works;
    private final String description;
    private final Set<Group> groups;

    @Data
    public static class Address {
        private final String street;
        private final String city;
        private final String zipCode;
        private final String postDist;
    }

    @Data
    public static class Work {
        private final String position;
        private final pl.kartven.universitier.domain.model.Work.Status status;
    }

    @Data
    public static class Group {
        private final Long moduleId;
        private final Long groupId;
        private final Long academicYearId;
    }
}
