package pl.kartven.universitier.application.usecase.staff;

import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.util.FilterParams;
import pl.kartven.universitier.domain.model.Module;
import pl.kartven.universitier.domain.model.*;
import pl.kartven.universitier.domain.repository.*;
import pl.kartven.universitier.infrastructure.staff.dto.StaffForPageResponse;
import pl.kartven.universitier.infrastructure.staff.dto.StaffViewResponse;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class StaffGetUseCase implements IStaffGetUseCase {
    private final StaffRepository repository;
    private final StaffMapper mapper;
    private final CourseRepository courseRepository;
    private final AcademicYearRepository academicYearRepository;
    private final ModuleRepository moduleRepository;
    private final ConnectionRepository connectionRepository;

    @Override
    public Either<ApiException, Page<StaffForPageResponse>> execute(
            FilterParams filterParams, PageRequest pageRequest
    ) {
        return Either.right(null);
    }

    private Page<Staff> executeInRepo(FilterParams filterParams, PageRequest pageRequest) {
        return Page.empty();
        /*return repository.findAllByCriteria(
                filterParams.getPhrase(),
                filterParams.getPhrase(),
                filterParams.getPhrase(),
                pageRequest
        );*/
    }

    private List<StaffForPageResponse> mapToStaffForPage(Page<Staff> pages) {
        return pages.stream().map(mapper::mapToForPage).toList();
    }

    @Override
    public Either<ApiException, StaffViewResponse> execute(Long id) {
        return Either.right(null);
    }

    @Mapper(componentModel = "spring")
    public interface StaffMapper {

        @Mapping(target = "coursesCount", constant = "0")
        @Mapping(target = "isActive", source = "user.enabled")
        StaffForPageResponse mapToForPage(Staff student);

        @Mapping(target = "isActive", source = "user.enabled")
        @Mapping(target = "email", source = "student", qualifiedByName = "mapEmail")
        @Mapping(target = "id", source = "id")
        @Mapping(target = "courses", expression = "java(new java.util.HashSet<>())")
        StaffViewResponse mapToView(Staff student);

        @Named("mapEmail")
        default String mapEmail(Staff student){
            return String.format("%s@universitier.edu.pl", student.getId());
        }

        default StaffViewResponse mapToView(Staff student, Set<Connection> connections, Set<Module> modules) {
            var view = mapToView(student);
            Map<Course, Map<Programme, List<Module>>> courses = modules.stream().collect(Collectors.groupingBy(
                    module -> module.getProgramme().getCourse(), Collectors.groupingBy(Module::getProgramme)
            ));
            var viewCourses = courses.entrySet().stream().map(courseEntry -> {
                var course = courseEntry.getKey();
                var viewProgramme = courseEntry.getValue().entrySet().stream().map(programmeEntry -> {
                    var programme = programmeEntry.getKey();
                    var viewModules = programmeEntry.getValue().stream().map(module -> {
                        var filteredConnections = connections.stream()
                                .filter(con -> con.getModule().getId().equals(module.getId()))
                                .toList();
                        var viewGroups = filteredConnections.stream()
                                .map(Connection::getGroup)
                                .map(group -> new StaffViewResponse.Course.Programme.Module.Group(
                                        group.getId(), group.getType(), group.getNumber()
                                ))
                                .collect(Collectors.toSet());
                        var viewAcaYear = filteredConnections.stream()
                                .filter(con -> con.getModule().getId().equals(module.getId()))
                                .map(Connection::getAcademicYear)
                                .findFirst()
                                .map(academicYear -> new StaffViewResponse.Course.Programme.Module.AcademicYear(
                                        academicYear.getId(), academicYear.getRange()
                                ));
                        return new StaffViewResponse.Course.Programme.Module(
                                module.getId(), module.getName(), viewAcaYear.orElse(null), viewGroups
                        );
                    }).collect(Collectors.toSet());
                    return new StaffViewResponse.Course.Programme(programme.getId(), programme.getName(), viewModules);
                }).findFirst();
                return new StaffViewResponse.Course(course.getId(), course.getName(), viewProgramme.orElse(null));
            }).collect(Collectors.toSet());
            view.getCourses().addAll(viewCourses);
            return view;
        }
    }
}
