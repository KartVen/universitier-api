package pl.kartven.universitier.application.usecase.faculty;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.exception.ResourceNotFoundException;
import pl.kartven.universitier.application.exception.ServerProcessingException;
import pl.kartven.universitier.domain.model.Faculty;
import pl.kartven.universitier.domain.repository.FacultyRepository;

@AllArgsConstructor
@Component
public class FacultyDeleteUseCase implements IFacultyDeleteUseCase {
    private final FacultyRepository repository;

    @Override
    public Either<ApiException, Void> execute(Long id) {
        return Option.ofOptional(repository.findById(id))
                .toEither(() -> (ApiException) new ResourceNotFoundException("Faculty not found: " + id))
                .flatMap(faculty -> Try.of(() -> {
                            repository.delete(new Faculty());
                            return true;
                        })
                        .toEither(new ServerProcessingException("Problem with deleting: " + id)))
                .map(s -> null);
    }
}
