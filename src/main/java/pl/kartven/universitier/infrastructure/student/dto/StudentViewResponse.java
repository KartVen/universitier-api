package pl.kartven.universitier.infrastructure.student.dto;

import lombok.Value;
import pl.kartven.universitier.domain.model.Group;

import java.util.Set;

@Value
public class StudentViewResponse {
    Long id;
    String firstName;
    String lastName;
    String email;
    Boolean isActive;
    Set<Course> courses;

    @Value
    public static class Course {
        Long id;
        String name;
        Programme programme;

        @Value
        public static class Programme {
            Long id;
            String name;
            Set<Module> modules;

            @Value
            public static class Module {
                Long id;
                String name;
                AcademicYear academicYear;
                Set<Group> groups;

                @Value
                public static class AcademicYear {
                    Long id;
                    String range;
                }

                @Value
                public static class Group {
                    Long id;
                    pl.kartven.universitier.domain.model.Group.Type type;
                    Integer number;
                }
            }
        }
    }
}
