package pl.kartven.universitier.application.usecase.academicyear;

import io.vavr.control.Either;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.infrastructure.common.dto.AddEditResponse;
import pl.kartven.universitier.infrastructure.academicyear.dto.AcademicYearAddEditRequest;

public interface IAcademicYearCreateUpdateUseCase {
    Either<ApiException, AddEditResponse> execute(AcademicYearAddEditRequest request);
    Either<ApiException, Void> execute(Long id, AcademicYearAddEditRequest request);
}
