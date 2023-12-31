package pl.kartven.universitier.application.usecase.connection;

import io.vavr.control.Either;
import pl.kartven.universitier.application.exception.ApiException;

public interface IConnectionDeleteUseCase {
    Either<ApiException, Void> execute(Long id);
}
