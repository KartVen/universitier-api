package pl.kartven.universitier.application.usecase.student;

import io.vavr.control.Either;
import io.vavr.control.Option;
import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.exception.ResourceNotFoundException;
import pl.kartven.universitier.application.exception.ServerProcessingException;
import pl.kartven.universitier.application.util.FilterParams;
import pl.kartven.universitier.domain.model.Module;
import pl.kartven.universitier.domain.model.*;
import pl.kartven.universitier.domain.repository.*;
import pl.kartven.universitier.infrastructure.student.dto.StudentForPageResponse;
import pl.kartven.universitier.infrastructure.student.dto.StudentViewResponse;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class StudentGetUseCase implements IStudentGetUseCase {
    private final StudentRepository repository;
    private final StudentMapper mapper;
    private final ModuleRepository moduleRepository;
    private final ConnectionRepository connectionRepository;

    @Override
    public Either<ApiException, Page<StudentForPageResponse>> execute(
            FilterParams filterParams, PageRequest pageRequest
    ) {
        return Option.of(executeInRepo(filterParams, pageRequest))
                .toEither((ApiException) new ServerProcessingException())
                .map(pages -> new PageImpl<>(
                        mapToStudentForPage(pages), pages.getPageable(), pages.getTotalElements()
                ));
    }

    private Page<Student> executeInRepo(FilterParams filterParams, PageRequest pageRequest) {
        return repository.findAllByCriteria(
                filterParams.getPhrase(),
                filterParams.getPhrase(),
                pageRequest
        );
    }

    private List<StudentForPageResponse> mapToStudentForPage(Page<Student> pages) {
        return pages.stream().map(mapper::mapToForPage).toList();
    }

    @Override
    public Either<ApiException, StudentViewResponse> execute(Long id) {
        var studentEither = Option.ofOptional(repository.findById(id))
                .toEither((ApiException) new ResourceNotFoundException("Student not found: " + id));
        if (studentEither.isLeft()) return Either.left(studentEither.getLeft());
        var student = studentEither.get();
        var connectionsEither = Option.of(connectionRepository.findAllByStudentId(student.getId()))
                .toEither((ApiException) new ServerProcessingException());
        if (connectionsEither.isLeft()) return Either.left(connectionsEither.getLeft());
        var modulesEither = Option.of(moduleRepository.findAllByConIdInWithConAndPAndC(
                connectionsEither.get().stream().map(Connection::getId).toList()
        )).toEither((ApiException) new ServerProcessingException());
        if (modulesEither.isLeft()) return Either.left(modulesEither.getLeft());
        return Either.right(mapper.mapToView(student, connectionsEither.get(), modulesEither.get()));
    }

    @Mapper(componentModel = "spring")
    public interface StudentMapper {

        @Mapping(target = "coursesCount", constant = "0")
        @Mapping(target = "isActive", source = "user.enabled")
        StudentForPageResponse mapToForPage(Student student);

        @Mapping(target = "isActive", source = "user.enabled")
        @Mapping(target = "email", source = "student", qualifiedByName = "mapEmail")
        @Mapping(target = "id", source = "id")
        @Mapping(target = "courses", expression = "java(new java.util.HashSet<>())")
        StudentViewResponse mapToView(Student student);

        @Named("mapEmail")
        default String mapEmail(Student student){
            return String.format("%s@universitier.edu.pl", student.getId());
        }

        default StudentViewResponse mapToView(Student student, Set<Connection> connections, Set<Module> modules) {
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
                                .map(group -> new StudentViewResponse.Course.Programme.Module.Group(
                                        group.getId(), group.getType(), group.getNumber()
                                ))
                                .collect(Collectors.toSet());
                        var viewAcaYear = filteredConnections.stream()
                                .filter(con -> con.getModule().getId().equals(module.getId()))
                                .map(Connection::getAcademicYear)
                                .findFirst()
                                .map(academicYear -> new StudentViewResponse.Course.Programme.Module.AcademicYear(
                                        academicYear.getId(), academicYear.getRange()
                                ));
                        return new StudentViewResponse.Course.Programme.Module(
                                module.getId(), module.getName(), viewAcaYear.orElse(null), viewGroups
                        );
                    }).collect(Collectors.toSet());
                    return new StudentViewResponse.Course.Programme(programme.getId(), programme.getName(), viewModules);
                }).findFirst();
                return new StudentViewResponse.Course(course.getId(), course.getName(), viewProgramme.orElse(null));
            }).collect(Collectors.toSet());
            view.getCourses().addAll(viewCourses);
            return view;
        }
    }
}
