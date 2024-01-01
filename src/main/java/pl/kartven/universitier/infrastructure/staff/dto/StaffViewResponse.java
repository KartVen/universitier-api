package pl.kartven.universitier.infrastructure.staff.dto;

import lombok.Value;

import java.util.Set;

@Value
public class StaffViewResponse {
    Long id;
    String firstName;
    String lastName;
    String email;
    Boolean isActive;
    Set<CourseDto> courses;
    Set<WorkDto> works;

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
                    pl.kartven.universitier.domain.model.Group.Type type;
                    Integer number;
                }
            }
        }
    }
    @Value
    public static class WorkDto {
        Long id;
        String positionName;
        StatusEDto status;
        FacultyDto faculty;

        public enum StatusEDto {
            PROGRESS,
            FINISHED
        }

        @Value
        public static class FacultyDto {
            Long id;
            String name;
        }
    }
}
