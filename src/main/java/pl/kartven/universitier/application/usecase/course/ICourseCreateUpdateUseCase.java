package pl.kartven.universitier.application.usecase.course;

import io.vavr.control.Either;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.infrastructure.common.dto.AddEditResponse;
import pl.kartven.universitier.infrastructure.course.dto.CourseAddEditRequest;

public interface ICourseCreateUpdateUseCase {
    Either<ApiException, AddEditResponse> execute(CourseAddEditRequest request);
    Either<ApiException, Void> execute(Long id, CourseAddEditRequest request);
}
