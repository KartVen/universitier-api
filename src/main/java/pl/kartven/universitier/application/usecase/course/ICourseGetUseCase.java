package pl.kartven.universitier.application.usecase.course;

import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.usecase.group.IGroupGetUseCase;
import pl.kartven.universitier.application.util.FilterParams;
import pl.kartven.universitier.infrastructure.course.dto.CourseBaseResponse;
import pl.kartven.universitier.infrastructure.course.dto.CourseForPageResponse;
import pl.kartven.universitier.infrastructure.course.dto.CourseViewResponse;
import pl.kartven.universitier.infrastructure.group.dto.GroupBaseResponse;

import java.util.List;

public interface ICourseGetUseCase {
    Either<ApiException, Page<CourseForPageResponse>> execute(
            FilterParams filterParams, PageRequest pageRequest
    );

    Either<ApiException, CourseViewResponse> execute(Long id);

    Either<ApiException, List<CourseBaseResponse>> execute(ICourseGetUseCase.SelectableParams params);

    @AllArgsConstructor
    @Getter
    class SelectableParams {
        private final Long programmeId;
        private final Long academicYearId;
    }
}
