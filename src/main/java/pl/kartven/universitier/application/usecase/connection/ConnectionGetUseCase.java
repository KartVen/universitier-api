package pl.kartven.universitier.application.usecase.connection;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.exception.ResourceNotFoundException;
import pl.kartven.universitier.application.exception.ServerProcessingException;
import pl.kartven.universitier.application.util.FilterParams;
import pl.kartven.universitier.domain.model.Connection;
import pl.kartven.universitier.domain.model.Group;
import pl.kartven.universitier.domain.repository.ConnectionRepository;
import pl.kartven.universitier.domain.repository.GroupRepository;
import pl.kartven.universitier.infrastructure.connection.dto.ConnectionForPageResponse;
import pl.kartven.universitier.infrastructure.connection.dto.ConnectionViewResponse;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class ConnectionGetUseCase implements IConnectionGetUseCase {
    private final ConnectionRepository repository;
    private final ConnectionMapper mapper;
    private final GroupRepository groupRepository;

    @Override
    public Either<ApiException, Page<ConnectionForPageResponse>> execute(
            FilterParams filterParams, PageRequest pageRequest
    ) {
        return Option.of(executeInRepo(filterParams, pageRequest))
                .toEither((ApiException) new ServerProcessingException())
                .map(pages -> new PageImpl<>(
                        mapToConnectionForPage(pages), pages.getPageable(), pages.getTotalElements()
                ));
    }

    private Page<Connection> executeInRepo(FilterParams filterParams, PageRequest pageRequest) {
        return repository.findAllByCriteria(
                filterParams.getPhrase(),
                filterParams.getPhrase(),
                pageRequest
        );
    }

    private List<ConnectionForPageResponse> mapToConnectionForPage(Page<Connection> pages) {
        return pages.stream().map(mapper::mapToForPage).toList();
    }

    @Override
    public Either<ApiException, ConnectionViewResponse> execute(Long id) {
        var connectionEither = Option.ofOptional(repository.findByIdWithMAndAC(id))
                .toEither((ApiException) new ResourceNotFoundException("Connection not found: " + id));
        if (connectionEither.isLeft()) return Either.left(connectionEither.getLeft());
        var connection = connectionEither.get();
        var groupsEither = Option.of(groupRepository.findAllByModuleIdAndAYId(connection.getModule().getId(), connection.getAcademicYear().getId()))
                .toEither((ApiException) new ServerProcessingException());
        if (groupsEither.isLeft()) return Either.left(groupsEither.getLeft());
        return Either.right(mapper.mapToView(connection, groupsEither.get()));
    }

    @Mapper(componentModel = "spring")
    public interface ConnectionMapper {
        @Mapping(target = "moduleName", source = "module.name")
        @Mapping(target = "academicYearRange", source = "academicYear.range")
        @Mapping(target = "groupName", source = "group", qualifiedByName = "mapGroup")
        ConnectionForPageResponse mapToForPage(Connection course);

        @Named("mapGroup")
        default String mapGroup(Group group){
            return group.getType().toString() + group.getNumber();
        }

        @Mapping(target = "groups", expression = "java(new java.util.HashSet<>())")
        ConnectionViewResponse mapToView(Connection connection);

        default ConnectionViewResponse mapToView(Connection connection, Set<Group> groups){
            var view = mapToView(connection);
            var viewGroups = groups.stream().map(group -> new ConnectionViewResponse.Group(
                    group.getId(), group.getType(), group.getNumber()
            )).toList();
            view.getGroups().addAll(viewGroups);
            return view;
        }
    }
}
