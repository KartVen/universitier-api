package pl.kartven.universitier.application.usecase.programme;

import io.vavr.control.Either;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.infrastructure.common.dto.AddEditResponse;
import pl.kartven.universitier.infrastructure.programme.dto.ProgrammeAddEditRequest;

public interface IProgrammeCreateUpdateUseCase {
    Either<ApiException, AddEditResponse> execute(ProgrammeAddEditRequest request);

    Either<ApiException, Void> execute(Long id, ProgrammeAddEditRequest request);
}
