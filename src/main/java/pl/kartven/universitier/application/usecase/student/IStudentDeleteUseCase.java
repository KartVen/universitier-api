package pl.kartven.universitier.application.usecase.student;

import io.vavr.control.Either;
import pl.kartven.universitier.application.exception.ApiException;

public interface IStudentDeleteUseCase {
    Either<ApiException, Void> execute(Long id);
}
