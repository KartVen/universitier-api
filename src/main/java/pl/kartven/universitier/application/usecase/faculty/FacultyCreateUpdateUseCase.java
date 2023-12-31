package pl.kartven.universitier.application.usecase.faculty;

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
import pl.kartven.universitier.domain.model.Faculty;
import pl.kartven.universitier.domain.repository.FacultyRepository;
import pl.kartven.universitier.infrastructure.common.dto.AddEditResponse;
import pl.kartven.universitier.infrastructure.faculty.dto.FacultyAddEditRequest;

@AllArgsConstructor
@Component
public class FacultyCreateUpdateUseCase implements IFacultyCreateUpdateUseCase {
    private final FacultyRepository repository;
    private FacultyMapper mapper;

    @Override
    public Either<ApiException, AddEditResponse> execute(FacultyAddEditRequest request) {
        return Try.of(() -> repository.save(mapper.map(request)))
                .toEither()
                .mapLeft(th -> (ApiException) new ServerProcessingException(th.getMessage()))
                .map(faculty -> new AddEditResponse(faculty.getId().toString(), faculty.getName()));
    }

    @Override
    public Either<ApiException, Void> execute(Long id, FacultyAddEditRequest request) {
        return Option.ofOptional(repository.findById(id))
                .toEither((ApiException) new ResourceNotFoundException("Faculty not found: " + id))
                .map(entity -> mapper.update(entity, request))
                .flatMap(entity -> Try.of(() -> repository.save(entity))
                        .toEither()
                        .mapLeft(e -> new ServerProcessingException(e.getMessage())))
                .map(entity -> null);
    }

    @Mapper(componentModel = "spring")
    public interface FacultyMapper {
        @Mapping(target = "id", ignore = true)
        @Mapping(target = "courses", ignore = true)
        @Mapping(target = "shortname", source = "shortName")
        Faculty map(FacultyAddEditRequest request);

        default Faculty update(Faculty entity, FacultyAddEditRequest request){
            entity.setName(request.getName());
            entity.setShortname(request.getShortName());
            entity.setYearFounded(request.getYearFounded());
            return entity;
        }
    }
}
