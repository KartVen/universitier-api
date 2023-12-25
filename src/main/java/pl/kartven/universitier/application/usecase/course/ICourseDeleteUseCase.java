package pl.kartven.universitier.application.usecase.course;

import io.vavr.control.Either;
import pl.kartven.universitier.application.exception.ApiException;

public interface ICourseDeleteUseCase {
    Either<ApiException, Void> execute(Long id);
}
