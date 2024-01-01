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
import pl.kartven.universitier.application.util.JpaHelper;
import pl.kartven.universitier.domain.model.*;
import pl.kartven.universitier.domain.model.Module;
import pl.kartven.universitier.domain.repository.*;
import pl.kartven.universitier.infrastructure.common.dto.AddEditResponse;
import pl.kartven.universitier.infrastructure.module.dto.ModuleAddEditRequest;

import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class ModuleCreateUpdateUseCase implements IModuleCreateUpdateUseCase {
    private final ModuleRepository repository;
    private final ProgrammeRepository programmeRepository;
    private final ModuleMapper mapper;
    private final AcademicYearRepository academicYearRepository;
    private final GroupRepository groupRepository;
    private final ConnectionRepository connectionRepository;

    @Override
    public Either<ApiException, AddEditResponse> execute(ModuleAddEditRequest request) {
        var programme = getProgramme(request.getProgrammeId());
        if (programme.isLeft()) return Either.left(programme.getLeft());

        var acaYearsEither = JpaHelper.getEntitiesSets(
                academicYearRepository::findDistinctByIdIn,
                request.getConnections().stream().map(ModuleAddEditRequest.ConnectionDto::getAcademicYearId).toList(),
                AcademicYear::getId, "Academic years"
        );
        if (acaYearsEither.isLeft()) return Either.left(acaYearsEither.getLeft());

        var acaYearMap = acaYearsEither.get().stream()
                .collect(Collectors.toMap(AcademicYear::getId, Function.identity()));

        var groupsEither = JpaHelper.getEntitiesSets(
                groupRepository::findDistinctByIdIn, request.getConnections().stream().map(ModuleAddEditRequest.ConnectionDto::getGroupId).toList(),
                Group::getId, "Groups"
        );
        if (groupsEither.isLeft()) return Either.left(groupsEither.getLeft());
        var groupMap = groupsEither.get().stream()
                .collect(Collectors.toMap(Group::getId, Function.identity()));

        var module = mapper.map(request);

        var connections = request.getConnections().stream().map(connection -> new Connection(
                module,
                acaYearMap.get(connection.getAcademicYearId()),
                groupMap.get(connection.getGroupId()))).collect(Collectors.toSet());

        module.setProgramme(programme.get());
        module.setConnections(connections);

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
                .flatMap(module -> {
                    var programme = getProgramme(request.getProgrammeId());
                    if (programme.isLeft()) return Either.left(programme.getLeft());

                    var acaYearsEither = JpaHelper.getEntitiesSets(
                            academicYearRepository::findDistinctByIdIn,
                            request.getConnections().stream().map(ModuleAddEditRequest.ConnectionDto::getAcademicYearId).toList(),
                            AcademicYear::getId, "Academic years"
                    );
                    if (acaYearsEither.isLeft()) return Either.left(acaYearsEither.getLeft());

                    var acaYearMap = acaYearsEither.get().stream()
                            .collect(Collectors.toMap(AcademicYear::getId, Function.identity()));

                    var groupsEither = JpaHelper.getEntitiesSets(
                            groupRepository::findDistinctByIdIn, request.getConnections().stream().map(ModuleAddEditRequest.ConnectionDto::getGroupId).toList(),
                            Group::getId, "Groups"
                    );
                    if (groupsEither.isLeft()) return Either.left(groupsEither.getLeft());
                    var groupMap = groupsEither.get().stream()
                            .collect(Collectors.toMap(Group::getId, Function.identity()));

                    var connections = request.getConnections().stream().map(connection -> new Connection(
                            module,
                            acaYearMap.get(connection.getAcademicYearId()),
                            groupMap.get(connection.getGroupId()))).collect(Collectors.toSet());

                    var updatedModule = mapper.update(module, request);
                    updatedModule.setProgramme(programme.get());
                    updatedModule.setConnections(connections);
                    return Either.right(module);
                })
                .flatMap(module -> Try.of(() -> repository.save(module))
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
            entity.setEcts(request.getEcts());
            entity.setIsExam(request.getIsExam());
            entity.setHours(request.getHours());
            return entity;
        }
    }

}
