package pl.kartven.universitier.application.usecase.student;

import io.vavr.control.Either;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.infrastructure.common.dto.AddEditResponse;
import pl.kartven.universitier.infrastructure.student.dto.StudentAddEditRequest;

public interface IStudentCreateUpdateUseCase {
    Either<ApiException, AddEditResponse> execute(StudentAddEditRequest request);
    Either<ApiException, Void> execute(Long id, StudentAddEditRequest request);
}
