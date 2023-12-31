package pl.kartven.universitier.application.usecase.module;

import io.vavr.control.Either;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.util.FilterParams;
import pl.kartven.universitier.infrastructure.academicyear.dto.AcademicYearBaseResponse;
import pl.kartven.universitier.infrastructure.module.dto.ModuleBaseResponse;
import pl.kartven.universitier.infrastructure.module.dto.ModuleForPageResponse;
import pl.kartven.universitier.infrastructure.module.dto.ModuleViewResponse;

import java.util.List;

public interface IModuleGetUseCase {
    Either<ApiException, Page<ModuleForPageResponse>> execute(
            FilterParams filterParams, PageRequest pageRequest
    );

    Either<ApiException, ModuleViewResponse> execute(Long id);

    Either<ApiException, List<ModuleBaseResponse>> execute();
}
