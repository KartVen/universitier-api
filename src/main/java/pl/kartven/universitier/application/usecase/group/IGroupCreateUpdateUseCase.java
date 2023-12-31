package pl.kartven.universitier.application.usecase.group;

import io.vavr.control.Either;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.infrastructure.common.dto.AddEditResponse;
import pl.kartven.universitier.infrastructure.group.dto.GroupAddEditRequest;

public interface IGroupCreateUpdateUseCase {
    Either<ApiException, AddEditResponse> execute(GroupAddEditRequest request);
    Either<ApiException, Void> execute(Long id, GroupAddEditRequest request);
}
