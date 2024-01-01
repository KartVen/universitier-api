package pl.kartven.universitier.application.usecase.academicyear;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.exception.ResourceNotFoundException;
import pl.kartven.universitier.application.exception.ServerProcessingException;
import pl.kartven.universitier.application.util.FilterParams;
import pl.kartven.universitier.domain.model.AcademicYear;
import pl.kartven.universitier.domain.repository.AcademicYearRepository;
import pl.kartven.universitier.infrastructure.academicyear.dto.AcademicYearBaseResponse;
import pl.kartven.universitier.infrastructure.academicyear.dto.AcademicYearForPageResponse;
import pl.kartven.universitier.infrastructure.academicyear.dto.AcademicYearViewResponse;

import java.util.List;

@AllArgsConstructor
@Component
public class AcademicYearGetUseCase implements IAcademicYearGetUseCase {
    private final AcademicYearRepository repository;
    private final SemesterMapper mapper;

    @Override
    public Either<ApiException, Page<AcademicYearForPageResponse>> execute(
            FilterParams filterParams, PageRequest pageRequest
    ) {
        return Option.of(executeInRepo(filterParams, pageRequest))
                .toEither((ApiException) new ServerProcessingException())
                .map(pages -> new PageImpl<>(
                        mapToSemesterForPage(pages), pages.getPageable(), pages.getTotalElements()
                ));
    }

    private Page<AcademicYear> executeInRepo(FilterParams filterParams, PageRequest pageRequest) {
        return repository.findAllByCriteria(
                filterParams.getPhrase(),
                pageRequest
        );
    }

    private List<AcademicYearForPageResponse> mapToSemesterForPage(Page<AcademicYear> pages) {
        return pages.stream().map(mapper::mapToForPage).toList();
    }

    @Override
    public Either<ApiException, AcademicYearViewResponse> execute(Long id) {
        return Option.ofOptional(repository.findById(id))
                .toEither((ApiException) new ResourceNotFoundException("Academic Year not found: " + id))
                .map(mapper::mapToView);
    }

    @Override
    public Either<ApiException, List<AcademicYearBaseResponse>> execute() {
        return Option.of(repository.findAll())
                .toEither((ApiException) new ServerProcessingException())
                .map(mapper::mapToListBase);
    }

    @Mapper(componentModel = "spring")
    public interface SemesterMapper {
        AcademicYearForPageResponse mapToForPage(AcademicYear academicYear);
        
        AcademicYearViewResponse mapToView(AcademicYear academicYear);

        List<AcademicYearBaseResponse> mapToListBase(List<AcademicYear> academicYears);
    }
}
