package pl.kartven.universitier.application.usecase.faculty;

import io.vavr.control.Either;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.infrastructure.common.dto.AddEditResponse;
import pl.kartven.universitier.infrastructure.faculty.dto.FacultyAddEditRequest;

public interface IFacultyCreateUpdateUseCase {
    Either<ApiException, AddEditResponse> execute(FacultyAddEditRequest request);
    Either<ApiException, Void> execute(Long id, FacultyAddEditRequest request);
}
