package pl.kartven.universitier.application.usecase.course;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.exception.ResourceNotFoundException;
import pl.kartven.universitier.application.exception.ServerProcessingException;
import pl.kartven.universitier.domain.model.AcademicYear;
import pl.kartven.universitier.domain.model.Course;
import pl.kartven.universitier.domain.repository.AcademicYearRepository;
import pl.kartven.universitier.domain.repository.CourseRepository;
import pl.kartven.universitier.domain.repository.FacultyRepository;
import pl.kartven.universitier.infrastructure.common.dto.AddEditResponse;
import pl.kartven.universitier.infrastructure.course.dto.CourseAddEditRequest;

import java.util.Set;

@AllArgsConstructor
@Component
public class CourseCreateUpdateUseCase implements ICourseCreateUpdateUseCase {
    private final CourseRepository repository;
    private final AcademicYearRepository academicYearRepository;
    private final FacultyRepository facultyRepository;
    private final CourseMapper mapper;

    @Override
    public Either<ApiException, AddEditResponse> execute(CourseAddEditRequest request) {
        var faculty = Option.ofOptional(facultyRepository.findById(request.getFacultyId()))
                .toEither((ApiException) new ResourceNotFoundException("Faculty not found: " + request.getFacultyId()));
        if (faculty.isLeft()) return Either.left(faculty.getLeft());
        var academicYears = fetchAcademicYears(request.getAcademicYearsIds());
        if (academicYears.isLeft()) return Either.left(academicYears.getLeft());

        var course = mapper.map(request);
        course.setFaculty(faculty.get());
        //course.getAcademicYears().addAll(academicYears.get());

        return Try.of(() -> repository.save(course))
                .toEither()
                .mapLeft(th -> (ApiException) new ServerProcessingException(th.getMessage()))
                .map(entity -> new AddEditResponse(entity.getId().toString(), entity.getName()));
    }

    @Override
    public Either<ApiException, Void> execute(Long id, CourseAddEditRequest request) {
        return Option.ofOptional(repository.findByIdWithFAndCN(id))
                .toEither((ApiException) new ResourceNotFoundException("Course not found: " + id))
                .map(entity -> mapper.update(entity, request, fetchAcademicYears(request.getAcademicYearsIds()).get()))
                .flatMap(entity -> Try.of(() -> repository.save(entity))
                        .toEither()
                        .mapLeft(e -> new ServerProcessingException(e.getMessage())))
                .map(entity -> null);
    }

    private Either<ApiException, Set<AcademicYear>> fetchAcademicYears(Set<Long> ids) {
        return Option.of(ids)
                .map(academicYearRepository::findDistinctByIdIn)
                .toEither(new ServerProcessingException());
    }

    @Mapper(componentModel = "spring")
    public interface CourseMapper {
        @Mapping(target = "id", ignore = true)
        @Mapping(target = "faculty", ignore = true)
        @Mapping(target = "programmes", ignore = true)
        Course map(CourseAddEditRequest request);

        default Course update(Course entity, CourseAddEditRequest request, Set<AcademicYear> academicYears) {
            entity.setName(request.getName());
            return entity;
        }
    }
}
