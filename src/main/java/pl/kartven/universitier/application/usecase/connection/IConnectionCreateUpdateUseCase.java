package pl.kartven.universitier.application.usecase.connection;

import io.vavr.control.Either;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.infrastructure.common.dto.AddEditResponse;
import pl.kartven.universitier.infrastructure.connection.dto.ConnectionAddEditRequest;

public interface IConnectionCreateUpdateUseCase {
    Either<ApiException, AddEditResponse> execute(ConnectionAddEditRequest request);
    Either<ApiException, Void> execute(Long id, ConnectionAddEditRequest request);
}
