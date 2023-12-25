package pl.kartven.universitier.application.usecase.faculty;

import io.vavr.control.Either;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.util.FilterParams;
import pl.kartven.universitier.infrastructure.faculty.dto.FacultyBaseResponse;
import pl.kartven.universitier.infrastructure.faculty.dto.FacultyForPageResponse;
import pl.kartven.universitier.infrastructure.faculty.dto.FacultyViewResponse;

import java.util.List;

public interface IFacultyGetUseCase {
    Either<ApiException, Page<FacultyForPageResponse>> execute(
            FilterParams filterParams, PageRequest pageRequest
    );

    Either<ApiException, FacultyViewResponse> execute(Long id);

    Either<ApiException, List<FacultyBaseResponse>> execute();
}
