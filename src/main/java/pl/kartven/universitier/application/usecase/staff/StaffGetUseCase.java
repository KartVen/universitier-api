package pl.kartven.universitier.application.usecase.staff;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.exception.ServerProcessingException;
import pl.kartven.universitier.application.util.FilterParams;
import pl.kartven.universitier.domain.model.Staff;
import pl.kartven.universitier.domain.repository.StaffRepository;
import pl.kartven.universitier.infrastructure.staff.dto.StaffForPageResponse;

import java.util.List;

@AllArgsConstructor
@Component
public class StaffGetUseCase {
    private final StaffRepository repository;
    private final StaffMapper mapper;

    public Either<ApiException, Page<StaffForPageResponse>> execute(
            FilterParams filterParams, PageRequest pageRequest
    ) {
        return Option.of(executeInRepo(filterParams, pageRequest))
                .toEither((ApiException) new ServerProcessingException())
                .map(pages -> new PageImpl<>(
                        mapToStaffForPage(pages), pages.getPageable(), pages.getTotalElements()
                ));
    }

    private List<StaffForPageResponse> mapToStaffForPage(Page<Staff> pages) {
        return pages.stream().map(mapper::map).toList();
    }

    private Page<Staff> executeInRepo(FilterParams filterParams, PageRequest pageRequest) {
        return repository.findAllByCriteria(
                filterParams.getPhrase(),
                filterParams.getPhrase(),
                filterParams.getPhrase(),
                pageRequest
        );
    }

    @Mapper(componentModel = "spring")
    public interface StaffMapper {
        StaffForPageResponse map(Staff student);
    }
}
