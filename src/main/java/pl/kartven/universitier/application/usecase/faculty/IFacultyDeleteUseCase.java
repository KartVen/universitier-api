package pl.kartven.universitier.application.usecase.faculty;

import io.vavr.control.Either;
import pl.kartven.universitier.application.exception.ApiException;

public interface IFacultyDeleteUseCase {
    Either<ApiException, Void> execute(Long id);
}
