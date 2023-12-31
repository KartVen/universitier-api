package pl.kartven.universitier.application.usecase.staff;

import io.vavr.control.Either;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.infrastructure.common.dto.AddEditResponse;
import pl.kartven.universitier.infrastructure.staff.dto.StaffAddEditRequest;

public interface IStaffCreateUpdateUseCase {
    Either<ApiException, AddEditResponse> execute(StaffAddEditRequest request);
    Either<ApiException, Void> execute(Long id, StaffAddEditRequest request);
}
