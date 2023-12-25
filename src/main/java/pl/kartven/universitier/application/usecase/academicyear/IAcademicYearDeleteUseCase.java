package pl.kartven.universitier.application.usecase.academicyear;

import io.vavr.control.Either;
import pl.kartven.universitier.application.exception.ApiException;

public interface IAcademicYearDeleteUseCase {
    Either<ApiException, Void> execute(Long id);
}
