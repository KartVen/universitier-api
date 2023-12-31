package pl.kartven.universitier.application.usecase.staff;

import io.vavr.control.Either;
import pl.kartven.universitier.application.exception.ApiException;

public interface IStaffDeleteUseCase {
    Either<ApiException, Void> execute(Long id);
}
