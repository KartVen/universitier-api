package pl.kartven.universitier.application.usecase.programme;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.exception.ResourceNotFoundException;
import pl.kartven.universitier.application.exception.ServerProcessingException;
import pl.kartven.universitier.application.util.FilterParams;
import pl.kartven.universitier.domain.model.Programme;
import pl.kartven.universitier.domain.repository.ProgrammeRepository;
import pl.kartven.universitier.infrastructure.programme.dto.ProgrammeForPageResponse;
import pl.kartven.universitier.infrastructure.programme.dto.ProgrammeViewResponse;

import java.util.List;

@AllArgsConstructor
@Component
public class ProgrammeGetUseCase implements IProgrammeGetUseCase {
    private final ProgrammeRepository repository;
    private final ProgrammeMapper mapper;

    @Override
    public Either<ApiException, Page<ProgrammeForPageResponse>> execute(
            FilterParams filterParams, PageRequest pageRequest
    ) {
        return Option.of(executeInRepo(filterParams, pageRequest))
                .toEither((ApiException) new ServerProcessingException())
                .map(pages -> new PageImpl<>(
                        mapToProgrammeForPage(pages), pages.getPageable(), pages.getTotalElements()
                ));
    }

    private Page<Programme> executeInRepo(FilterParams filterParams, PageRequest pageRequest) {
        return repository.findAllByCriteria(
                filterParams.getPhrase(),
                filterParams.getPhrase(),
                filterParams.getPhrase(),
                pageRequest
        );
    }

    private List<ProgrammeForPageResponse> mapToProgrammeForPage(Page<Programme> pages) {
        return pages.stream().map(mapper::mapToForPage).toList();
    }

    @Override
    public Either<ApiException, ProgrammeViewResponse> execute(Long id) {
        return Option.ofOptional(repository.findByIdWithCAndM(id))
                .toEither((ApiException) new ResourceNotFoundException("Programme not found: " + id))
                .map(mapper::mapToView);
    }

    @Mapper(componentModel = "spring")
    public interface ProgrammeMapper {

        @Mapping(target = "courseName", source = "course.name")
        @Mapping(target = "shortName", source = "shortname")
        ProgrammeForPageResponse mapToForPage(Programme programme);

        @Mapping(target = "shortName", source = "shortname")
        ProgrammeViewResponse mapToView(Programme programme);
    }
}
