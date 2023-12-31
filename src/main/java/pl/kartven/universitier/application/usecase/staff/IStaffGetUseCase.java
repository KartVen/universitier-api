package pl.kartven.universitier.application.usecase.staff;

import io.vavr.control.Either;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.util.FilterParams;
import pl.kartven.universitier.infrastructure.staff.dto.StaffForPageResponse;
import pl.kartven.universitier.infrastructure.staff.dto.StaffViewResponse;

public interface IStaffGetUseCase {
    Either<ApiException, Page<StaffForPageResponse>> execute(
            FilterParams filterParams, PageRequest pageRequest
    );

    Either<ApiException, StaffViewResponse> execute(Long id);
}
