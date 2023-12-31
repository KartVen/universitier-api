package pl.kartven.universitier.application.usecase.module;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
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
import pl.kartven.universitier.domain.model.Module;
import pl.kartven.universitier.domain.repository.ModuleRepository;
import pl.kartven.universitier.infrastructure.academicyear.dto.AcademicYearBaseResponse;
import pl.kartven.universitier.infrastructure.faculty.dto.FacultyBaseResponse;
import pl.kartven.universitier.infrastructure.module.dto.ModuleBaseResponse;
import pl.kartven.universitier.infrastructure.module.dto.ModuleForPageResponse;
import pl.kartven.universitier.infrastructure.module.dto.ModuleViewResponse;

import java.util.List;

@AllArgsConstructor
@Component
public class ModuleGetUseCase implements IModuleGetUseCase {
    private final ModuleRepository repository;
    private final ModuleMapper mapper;

    @Override
    public Either<ApiException, Page<ModuleForPageResponse>> execute(
            FilterParams filterParams, PageRequest pageRequest
    ) {
        return Option.of(executeInRepo(filterParams, pageRequest))
                .toEither((ApiException) new ServerProcessingException())
                .map(pages -> new PageImpl<>(
                        mapToModuleForPage(pages), pages.getPageable(), pages.getTotalElements()
                ));
    }

    private Page<Module> executeInRepo(FilterParams filterParams, PageRequest pageRequest) {
        return repository.findAllByCriteria(
                filterParams.getPhrase(),
                Try.of(() -> Integer.parseInt(filterParams.getPhrase())).getOrNull(),
                null,
                filterParams.getPhrase(),
                pageRequest
        );
    }

    private List<ModuleForPageResponse> mapToModuleForPage(Page<Module> pages) {
        return pages.stream().map(mapper::mapToForPage).toList();
    }

    @Override
    public Either<ApiException, ModuleViewResponse> execute(Long id) {
        return Option.ofOptional(repository.findByIdWithPAndCN(id))
                .toEither((ApiException) new ResourceNotFoundException("Module not found: " + id))
                .map(mapper::mapToView);
    }

    @Override
    public Either<ApiException, List<ModuleBaseResponse>> execute() {
        return Option.of(repository.findAll())
                .toEither((ApiException) new ServerProcessingException())
                .map(mapper::mapToListBase);
    }


    @Mapper(componentModel = "spring")
    public interface ModuleMapper {
        @Mapping(target = "programmeName", source = "programme.name")
        ModuleForPageResponse mapToForPage(Module programme);

        ModuleViewResponse mapToView(Module programme);

        List<ModuleBaseResponse> mapToListBase(List<Module> faculties);
    }
}
