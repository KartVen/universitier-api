package pl.kartven.universitier.application.usecase.programme;

import io.vavr.control.Either;
import pl.kartven.universitier.application.exception.ApiException;

public interface IProgrammeDeleteUseCase {
    Either<ApiException, Void> execute(Long id);
}
