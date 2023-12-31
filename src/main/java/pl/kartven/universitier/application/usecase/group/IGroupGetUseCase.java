package pl.kartven.universitier.application.usecase.group;

import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.util.FilterParams;
import pl.kartven.universitier.infrastructure.group.dto.GroupBaseResponse;
import pl.kartven.universitier.infrastructure.group.dto.GroupForPageResponse;
import pl.kartven.universitier.infrastructure.group.dto.GroupViewResponse;

import java.util.List;

public interface IGroupGetUseCase {
    Either<ApiException, Page<GroupForPageResponse>> execute(
            FilterParams filterParams, PageRequest pageRequest
    );

    Either<ApiException, GroupViewResponse> execute(Long id);

    Either<ApiException, List<GroupBaseResponse>> execute(SelectableParams params);

    @AllArgsConstructor
    @Getter
    class SelectableParams {
        private final Long moduleId;
        private final Long academicYearId;
    }
}
