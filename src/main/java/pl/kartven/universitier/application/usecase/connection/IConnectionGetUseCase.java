package pl.kartven.universitier.application.usecase.connection;

import io.vavr.control.Either;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.util.FilterParams;
import pl.kartven.universitier.infrastructure.connection.dto.ConnectionForPageResponse;
import pl.kartven.universitier.infrastructure.connection.dto.ConnectionViewResponse;

public interface IConnectionGetUseCase {
    Either<ApiException, Page<ConnectionForPageResponse>> execute(
            FilterParams filterParams, PageRequest pageRequest
    );

    Either<ApiException, ConnectionViewResponse> execute(Long id);
}
