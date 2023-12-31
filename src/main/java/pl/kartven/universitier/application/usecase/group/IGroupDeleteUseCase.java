package pl.kartven.universitier.application.usecase.group;

import io.vavr.control.Either;
import pl.kartven.universitier.application.exception.ApiException;

public interface IGroupDeleteUseCase {
    Either<ApiException, Void> execute(Long id);
}
