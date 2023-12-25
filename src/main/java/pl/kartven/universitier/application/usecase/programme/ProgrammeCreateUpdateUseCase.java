package pl.kartven.universitier.application.usecase.programme;

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
import pl.kartven.universitier.domain.model.Programme;
import pl.kartven.universitier.domain.repository.AcademicYearRepository;
import pl.kartven.universitier.domain.repository.CourseRepository;
import pl.kartven.universitier.domain.repository.ProgrammeRepository;
import pl.kartven.universitier.infrastructure.common.dto.AddEditResponse;
import pl.kartven.universitier.infrastructure.programme.dto.ProgrammeAddEditRequest;

import java.util.Set;

@AllArgsConstructor
@Component
public class ProgrammeCreateUpdateUseCase implements IProgrammeCreateUpdateUseCase {
    private final ProgrammeRepository repository;
    private final CourseRepository courseRepository;
    private final AcademicYearRepository academicYearRepository;
    private final ProgrammeMapper mapper;

    @Override
    public Either<ApiException, AddEditResponse> execute(ProgrammeAddEditRequest request) {
        var course = Option.ofOptional(courseRepository.findById(request.getCourseId()))
                .toEither((ApiException) new ResourceNotFoundException("Course not found: " + request.getCourseId()));
        if (course.isLeft()) return Either.left(course.getLeft());
        var academicYears = fetchAcademicYears(request.getAcademicYearsIds());
        if (academicYears.isLeft()) return Either.left(academicYears.getLeft());

        var programme = mapper.map(request);
        programme.setCourse(course.get());
        programme.getAcademicYears().addAll(academicYears.get());

        return Try.of(() -> repository.save(programme))
                .toEither()
                .mapLeft(th -> (ApiException) new ServerProcessingException(th.getMessage()))
                .map(entity -> new AddEditResponse(entity.getId().toString(), entity.getName()));
    }

    @Override
    public Either<ApiException, Void> execute(Long id, ProgrammeAddEditRequest request) {
        return Option.ofOptional(repository.findByIdWithCAndAY(id))
                .toEither((ApiException) new ResourceNotFoundException("Programme not found: " + id))
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
    public interface ProgrammeMapper {
        @Mapping(target = "id", ignore = true)
        @Mapping(target = "course", ignore = true)
        @Mapping(target = "modules", ignore = true)
        @Mapping(target = "academicYears", ignore = true)
        Programme map(ProgrammeAddEditRequest request);

        default Programme update(Programme entity, ProgrammeAddEditRequest request, Set<AcademicYear> academicYears) {
            entity.setName(request.getName());
            var academicYearsStrings = entity.getAcademicYears().stream()
                    .map(AcademicYear::getAcademicYear)
                    .toList();
            entity.getAcademicYears().addAll(academicYears.stream()
                    .filter(academicYear -> !academicYearsStrings.contains(academicYear.getAcademicYear()))
                    .toList());
            return entity;
        }
    }

}
