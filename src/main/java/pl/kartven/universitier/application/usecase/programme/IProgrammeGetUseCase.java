package pl.kartven.universitier.application.usecase.programme;

import io.vavr.control.Either;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.util.FilterParams;
import pl.kartven.universitier.infrastructure.programme.dto.ProgrammeForPageResponse;
import pl.kartven.universitier.infrastructure.programme.dto.ProgrammeViewResponse;

public interface IProgrammeGetUseCase {
    Either<ApiException, Page<ProgrammeForPageResponse>> execute(
            FilterParams filterParams, PageRequest pageRequest
    );

    Either<ApiException, ProgrammeViewResponse> execute(Long id);
}
