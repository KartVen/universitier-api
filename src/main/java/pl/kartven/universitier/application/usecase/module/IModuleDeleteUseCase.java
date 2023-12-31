package pl.kartven.universitier.application.usecase.module;

import io.vavr.control.Either;
import pl.kartven.universitier.application.exception.ApiException;

public interface IModuleDeleteUseCase {
    Either<ApiException, Void> execute(Long id);
}
