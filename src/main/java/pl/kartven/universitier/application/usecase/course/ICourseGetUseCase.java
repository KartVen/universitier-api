package pl.kartven.universitier.application.usecase.course;

import io.vavr.control.Either;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.util.FilterParams;
import pl.kartven.universitier.infrastructure.course.dto.CourseForPageResponse;
import pl.kartven.universitier.infrastructure.course.dto.CourseViewResponse;

public interface ICourseGetUseCase {
    Either<ApiException, Page<CourseForPageResponse>> execute(
            FilterParams filterParams, PageRequest pageRequest
    );

    Either<ApiException, CourseViewResponse> execute(Long id);
}
