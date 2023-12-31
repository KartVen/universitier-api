package pl.kartven.universitier.application.usecase.student;

import io.vavr.control.Either;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.util.FilterParams;
import pl.kartven.universitier.infrastructure.student.dto.StudentForPageResponse;
import pl.kartven.universitier.infrastructure.student.dto.StudentViewResponse;

public interface IStudentGetUseCase {
    Either<ApiException, Page<StudentForPageResponse>> execute(
            FilterParams filterParams, PageRequest pageRequest
    );

    Either<ApiException, StudentViewResponse> execute(Long id);
}
