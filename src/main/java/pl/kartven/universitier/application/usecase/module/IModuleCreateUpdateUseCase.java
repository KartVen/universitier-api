package pl.kartven.universitier.application.usecase.module;

import io.vavr.control.Either;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.infrastructure.common.dto.AddEditResponse;
import pl.kartven.universitier.infrastructure.module.dto.ModuleAddEditRequest;

public interface IModuleCreateUpdateUseCase {
    Either<ApiException, AddEditResponse> execute(ModuleAddEditRequest request);

    Either<ApiException, Void> execute(Long id, ModuleAddEditRequest request);
}
