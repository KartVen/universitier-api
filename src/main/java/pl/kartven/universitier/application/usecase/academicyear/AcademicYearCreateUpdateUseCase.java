package pl.kartven.universitier.application.usecase.academicyear;

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
import pl.kartven.universitier.domain.repository.AcademicYearRepository;
import pl.kartven.universitier.infrastructure.academicyear.dto.AcademicYearAddEditRequest;
import pl.kartven.universitier.infrastructure.common.dto.AddEditResponse;

@AllArgsConstructor
@Component
public class AcademicYearCreateUpdateUseCase implements IAcademicYearCreateUpdateUseCase {
    private final AcademicYearRepository repository;
    private SemesterMapper mapper;

    @Override
    public Either<ApiException, AddEditResponse> execute(AcademicYearAddEditRequest request) {
        return Try.of(() -> repository.save(mapper.map(request)))
                .toEither()
                .mapLeft(th -> (ApiException) new ServerProcessingException(th.getMessage()))
                .map(academicYear -> new AddEditResponse(academicYear.getId().toString(), academicYear.getMark()));
    }

    @Override
    public Either<ApiException, Void> execute(Long id, AcademicYearAddEditRequest request) {
        return Option.ofOptional(repository.findById(id))
                .toEither((ApiException) new ResourceNotFoundException("Academic Year not found: " + id))
                .map(entity -> mapper.update(entity, request))
                .flatMap(entity -> Try.of(() -> repository.save(entity))
                        .toEither()
                        .mapLeft(e -> new ServerProcessingException(e.getMessage())))
                .map(entity -> null);
    }

    @Mapper(componentModel = "spring")
    public interface SemesterMapper {
        @Mapping(target = "id", ignore = true)
        @Mapping(target = "connections", ignore = true)
        AcademicYear map(AcademicYearAddEditRequest request);

        default AcademicYear update(AcademicYear entity, AcademicYearAddEditRequest request){
            entity.setMark(request.getMark());
            entity.setIsClosed(request.getIsClosed());
            return entity;
        }
    }
}
