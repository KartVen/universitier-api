package pl.kartven.universitier.application.usecase.group;

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
import pl.kartven.universitier.domain.model.Group;
import pl.kartven.universitier.domain.repository.GroupRepository;
import pl.kartven.universitier.infrastructure.common.dto.AddEditResponse;
import pl.kartven.universitier.infrastructure.group.dto.GroupAddEditRequest;

@AllArgsConstructor
@Component
public class GroupCreateUpdateUseCase implements IGroupCreateUpdateUseCase {
    private final GroupRepository repository;
    private GroupMapper mapper;

    @Override
    public Either<ApiException, AddEditResponse> execute(GroupAddEditRequest request) {
        return Try.of(() -> repository.save(mapper.map(request)))
                .toEither()
                .mapLeft(th -> (ApiException) new ServerProcessingException(th.getMessage()))
                .map(group -> new AddEditResponse(group.getId().toString(), group.getType().toString() + group.getNumber()));
    }

    @Override
    public Either<ApiException, Void> execute(Long id, GroupAddEditRequest request) {
        return Option.ofOptional(repository.findById(id))
                .toEither((ApiException) new ResourceNotFoundException("Group not found: " + id))
                .map(entity -> mapper.update(entity, request))
                .flatMap(entity -> Try.of(() -> repository.save(entity))
                        .toEither()
                        .mapLeft(e -> new ServerProcessingException(e.getMessage())))
                .map(entity -> null);
    }

    @Mapper(componentModel = "spring")
    public interface GroupMapper {
        @Mapping(target = "id", ignore = true)
        @Mapping(target = "connections", ignore = true)
        //@Mapping(target = "student", ignore = true)
        Group map(GroupAddEditRequest request);

        default Group update(Group entity, GroupAddEditRequest request){
            entity.setType(request.getType());
            entity.setNumber(request.getNumber());
            return entity;
        }
    }
}
