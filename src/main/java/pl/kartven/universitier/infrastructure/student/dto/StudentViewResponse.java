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
    Set<CourseDto> courses;

    @Value
    public static class CourseDto {
        Long id;
        String name;
        ProgrammeDto programme;

        @Value
        public static class ProgrammeDto {
            Long id;
            String name;
            Set<ModuleDto> modules;

            @Value
            public static class ModuleDto {
                Long id;
                String name;
                AcademicYearDto academicYear;
                Set<GroupDto> groups;

                @Value
                public static class AcademicYearDto {
                    Long id;
                    String range;
                }

                @Value
                public static class GroupDto {
                    Long id;
                    Group.Type type;
                    Integer number;
                }
            }
        }
    }
}
