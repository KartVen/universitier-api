package pl.kartven.universitier.application.usecase.course;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.exception.ResourceNotFoundException;
import pl.kartven.universitier.application.exception.ServerProcessingException;
import pl.kartven.universitier.domain.model.Course;
import pl.kartven.universitier.domain.repository.CourseRepository;

@AllArgsConstructor
@Component
public class CourseDeleteUseCase implements ICourseDeleteUseCase {
    private final CourseRepository repository;

    @Override
    public Either<ApiException, Void> execute(Long id) {
        return Option.ofOptional(repository.findById(id))
                .toEither(() -> (ApiException) new ResourceNotFoundException("Course not found: " + id))
                .flatMap(faculty -> Try.of(() -> {
                            repository.delete(new Course());
                            return true;
                        })
                        .toEither(new ServerProcessingException("Problem with deleting: " + id)))
                .map(s -> null);
    }
}
