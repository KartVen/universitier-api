package pl.kartven.universitier.application.usecase.module;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.exception.ResourceNotFoundException;
import pl.kartven.universitier.application.exception.ServerProcessingException;
import pl.kartven.universitier.domain.model.Module;
import pl.kartven.universitier.domain.model.Programme;
import pl.kartven.universitier.domain.repository.ModuleRepository;
import pl.kartven.universitier.domain.repository.ProgrammeRepository;
import pl.kartven.universitier.infrastructure.common.dto.AddEditResponse;
import pl.kartven.universitier.infrastructure.module.dto.ModuleAddEditRequest;

@AllArgsConstructor
@Component
public class ModuleCreateUpdateUseCase implements IModuleCreateUpdateUseCase {
    private final ModuleRepository repository;
    private final ProgrammeRepository programmeRepository;
    private final ModuleMapper mapper;

    @Override
    public Either<ApiException, AddEditResponse> execute(ModuleAddEditRequest request) {
        var programme = getProgramme(request.getProgrammeId());
        if (programme.isLeft()) return Either.left(programme.getLeft());
        var module = mapper.map(request);
        module.setProgramme(programme.get());

        return Try.of(() -> repository.save(module))
                .toEither()
                .mapLeft(th -> (ApiException) new ServerProcessingException(th.getMessage()))
                .map(entity -> new AddEditResponse(entity.getId().toString(), entity.getName()));
    }

    private Either<ApiException, Programme> getProgramme(Long id) {
        return Option.ofOptional(programmeRepository.findById(id))
                .toEither((ApiException) new ResourceNotFoundException("Programme not found: " + id));
    }

    @Override
    public Either<ApiException, Void> execute(Long id, ModuleAddEditRequest request) {
        return Option.ofOptional(repository.findByIdWithPAndCN(id))
                .toEither((ApiException) new ResourceNotFoundException("Module not found: " + id))
                .map(entity -> mapper.update(entity, request))
                .flatMap(entity -> Try.of(() -> repository.save(entity))
                        .toEither()
                        .mapLeft(e -> new ServerProcessingException(e.getMessage())))
                .map(entity -> null);
    }

    @Mapper(componentModel = "spring")
    public interface ModuleMapper {
        @Mapping(target = "id", ignore = true)
        @Mapping(target = "programme", ignore = true)
        @Mapping(target = "connections", ignore = true)
        Module map(ModuleAddEditRequest request);

        default Module update(Module entity, ModuleAddEditRequest request) {
            entity.setName(request.getName());
            return entity;
        }
    }

}
