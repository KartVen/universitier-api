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
import pl.kartven.universitier.domain.model.Module;
import pl.kartven.universitier.domain.model.Programme;
import pl.kartven.universitier.domain.repository.AcademicYearRepository;
import pl.kartven.universitier.domain.repository.ModuleRepository;
import pl.kartven.universitier.infrastructure.module.dto.ModuleBaseResponse;
import pl.kartven.universitier.infrastructure.module.dto.ModuleForPageResponse;
import pl.kartven.universitier.infrastructure.module.dto.ModuleViewResponse;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

@AllArgsConstructor
@Component
public class ModuleGetUseCase implements IModuleGetUseCase {
    private final ModuleRepository repository;
    private final ModuleMapper mapper;
    private final AcademicYearRepository academicYearRepository;

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
        return pages.stream().map(programme -> mapper.mapToForPage(programme, academicYearRepository::findAllMarkByModuleId)).toList();
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
        default ModuleForPageResponse mapToForPage(Module programme, Function<Long, Set<String>> findAllMarkByModuleId){
            return new ModuleForPageResponse(
                    programme.getId(),
                    programme.getName(),
                    programme.getEcts(),
                    programme.getIsExam(),
                    programme.getName(),
                    findAllMarkByModuleId.apply(programme.getId())
            );
        }

        ModuleViewResponse mapToView(Module module);

        List<ModuleBaseResponse> mapToListBase(List<Module> faculties);
    }
}
