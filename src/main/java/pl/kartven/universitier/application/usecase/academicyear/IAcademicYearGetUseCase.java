package pl.kartven.universitier.application.usecase.academicyear;

import io.vavr.control.Either;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.util.FilterParams;
import pl.kartven.universitier.infrastructure.academicyear.dto.AcademicYearBaseResponse;
import pl.kartven.universitier.infrastructure.academicyear.dto.AcademicYearForPageResponse;
import pl.kartven.universitier.infrastructure.academicyear.dto.AcademicYearViewResponse;

import java.util.List;

public interface IAcademicYearGetUseCase {
    Either<ApiException, Page<AcademicYearForPageResponse>> execute(
            FilterParams filterParams, PageRequest pageRequest
    );

    Either<ApiException, AcademicYearViewResponse> execute(Long id);

    Either<ApiException, List<AcademicYearBaseResponse>> execute();
}
