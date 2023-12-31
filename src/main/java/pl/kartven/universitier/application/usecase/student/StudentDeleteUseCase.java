package pl.kartven.universitier.application.usecase.student;

import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.kartven.universitier.application.exception.ApiException;

@Component
@AllArgsConstructor
public class StudentDeleteUseCase implements IStudentDeleteUseCase {
    @Override
    public Either<ApiException, Void> execute(Long id) {
        return Either.right(null);
    }
}
