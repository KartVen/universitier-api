package pl.kartven.universitier.application.usecase.faculty;

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
import pl.kartven.universitier.domain.model.Faculty;
import pl.kartven.universitier.domain.repository.FacultyRepository;
import pl.kartven.universitier.infrastructure.faculty.dto.FacultyBaseResponse;
import pl.kartven.universitier.infrastructure.faculty.dto.FacultyForPageResponse;
import pl.kartven.universitier.infrastructure.faculty.dto.FacultyViewResponse;

import java.util.List;

@AllArgsConstructor
@Component
public class FacultyGetUseCase implements IFacultyGetUseCase {
    private final FacultyRepository repository;
    private final FacultyMapper mapper;

    @Override
    public Either<ApiException, Page<FacultyForPageResponse>> execute(
            FilterParams filterParams, PageRequest pageRequest
    ) {
        return Option.of(executeInRepo(filterParams, pageRequest))
                .toEither((ApiException) new ServerProcessingException())
                .map(pages -> new PageImpl<>(
                        mapToFacultyForPage(pages), pages.getPageable(), pages.getTotalElements()
                ));
    }

    private List<FacultyForPageResponse> mapToFacultyForPage(Page<Faculty> pages) {
        return pages.stream().map(mapper::mapToForPage).toList();
    }

    private Page<Faculty> executeInRepo(FilterParams filterParams, PageRequest pageRequest) {
        return repository.findAllByCriteria(
                filterParams.getPhrase(),
                filterParams.getPhrase(),
                pageRequest
        );
    }

    @Override
    public Either<ApiException, FacultyViewResponse> execute(Long id) {
        return Option.ofOptional(repository.findById(id))
                .toEither((ApiException) new ResourceNotFoundException("Faculty not found: " + id))
                .map(mapper::mapToView);
    }

    @Override
    public Either<ApiException, List<FacultyBaseResponse>> execute() {
        return Option.of(repository.findAll())
                .toEither((ApiException) new ServerProcessingException())
                .map(mapper::mapToListBase);
    }

    @Mapper(componentModel = "spring")
    public interface FacultyMapper {
        @Mapping(target = "shortName", source = "shortname")
        FacultyForPageResponse mapToForPage(Faculty faculty);

        @Mapping(target = "shortName", source = "shortname")
        FacultyViewResponse mapToView(Faculty faculty);

        List<FacultyBaseResponse> mapToListBase(List<Faculty> faculties);

        @Mapping(target = "shortName", source = "shortname")
        FacultyBaseResponse mapToBase(Faculty faculty);
    }
}
