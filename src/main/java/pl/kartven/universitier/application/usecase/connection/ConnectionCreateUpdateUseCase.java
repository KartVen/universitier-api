package pl.kartven.universitier.application.usecase.connection;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.exception.ResourceNotFoundException;
import pl.kartven.universitier.application.exception.ServerProcessingException;
import pl.kartven.universitier.domain.model.AcademicYear;
import pl.kartven.universitier.domain.model.Connection;
import pl.kartven.universitier.domain.model.Group;
import pl.kartven.universitier.domain.model.Module;
import pl.kartven.universitier.domain.repository.AcademicYearRepository;
import pl.kartven.universitier.domain.repository.ConnectionRepository;
import pl.kartven.universitier.domain.repository.GroupRepository;
import pl.kartven.universitier.domain.repository.ModuleRepository;
import pl.kartven.universitier.infrastructure.common.dto.AddEditResponse;
import pl.kartven.universitier.infrastructure.connection.dto.ConnectionAddEditRequest;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class ConnectionCreateUpdateUseCase implements IConnectionCreateUpdateUseCase {
    private final ConnectionRepository repository;
    private final ModuleRepository moduleRepository;
    private final AcademicYearRepository academicYearRepository;
    private final ConnectionMapper mapper;
    private final GroupRepository groupRepository;

    @Override
    public Either<ApiException, AddEditResponse> execute(ConnectionAddEditRequest request) {
        var module = getModule(request.getModuleId());
        if (module.isLeft()) return Either.left(module.getLeft());
        var academicYear = getAcademicYear(request.getAcademicYearId());
        if (academicYear.isLeft()) return Either.left(academicYear.getLeft());
        var groups = getGroups(request.getGroupsIds());
        if (groups.isLeft()) return Either.left(groups.getLeft());

        return Try.of(() -> repository.saveAll(mapper.map(module.get(), academicYear.get(), groups.get())))
                .toEither()
                .mapLeft(th -> (ApiException) new ServerProcessingException(th.getMessage()))
                .map(entities -> new AddEditResponse(entities.get(0).getId().toString(),
                        String.format("%s (%s)", module.get().getName(), academicYear.get().getRange())
                ));
    }

    private Either<ApiException, Module> getModule(Long id) {
        return Option.ofOptional(moduleRepository.findById(id))
                .toEither(new ResourceNotFoundException("Module not found: " + id));
    }

    private Either<ApiException, AcademicYear> getAcademicYear(Long id) {
        return Option.ofOptional(academicYearRepository.findById(id))
                .toEither(new ResourceNotFoundException("Academic Year not found: " + id));
    }

    private Either<ApiException, Set<Group>> getGroups(Set<Long> ids) {
        var groupsEither = Option.of(ids)
                .map(groupRepository::findDistinctByIdIn)
                .toEither((ApiException) new ServerProcessingException());
        if (groupsEither.isLeft()) return groupsEither;
        var groups = groupsEither.get();
        if (groups.size() != ids.size()) {
            var missingIds = groups.stream().map(Group::getId)
                    .filter(groupId -> !ids.contains(groupId))
                    .toList();
            groupsEither.mapLeft(e -> new ResourceNotFoundException("Groups not found: " + missingIds));
        }
        return groupsEither;
    }

    @Override
    public Either<ApiException, Void> execute(Long id, ConnectionAddEditRequest request) {
        var module = getModule(request.getModuleId());
        if (module.isLeft()) return Either.left(module.getLeft());
        var academicYear = getAcademicYear(request.getAcademicYearId());
        if (academicYear.isLeft()) return Either.left(academicYear.getLeft());
        var groups = getGroups(request.getGroupsIds());
        if (groups.isLeft()) return Either.left(groups.getLeft());

        return Option.of(repository.findAllByModuleIdAndAYIdWithMAndACAndG(id,academicYear.get().getId()))
                .toEither((ApiException) new ResourceNotFoundException("Connection not found: " + id))
                .map(entities -> mapper.update(entities, module.get(), academicYear.get(), groups.get()))
                .flatMap(entities -> Try.of(() -> repository.saveAll(entities))
                        .toEither()
                        .mapLeft(e -> new ServerProcessingException(e.getMessage())))
                .map(entity -> null);
    }

    @Mapper(componentModel = "spring")
    public interface ConnectionMapper {
        default Set<Connection> map(Module module, AcademicYear academicYear, Collection<Group> groups) {
            return groups.stream().map(group -> {
                var entity = new Connection();
                entity.setModule(module);
                entity.setAcademicYear(academicYear);
                entity.setGroup(group);
                return entity;
            }).collect(Collectors.toSet());
        }

        default Set<Connection> update(Collection<Connection> entities, Module module, AcademicYear academicYear, Set<Group> groups) {
            var dbGroupsIds = entities.stream().map(Connection::getGroup).map(Group::getId).toList();
            var newGroups = groups.stream()
                    .filter(newGroup -> !dbGroupsIds.contains(newGroup.getId()))
                    .toList();
            return map(module, academicYear, newGroups);
        }
    }
}
