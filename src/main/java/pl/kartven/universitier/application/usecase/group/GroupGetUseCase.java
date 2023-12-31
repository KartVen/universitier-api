package pl.kartven.universitier.application.usecase.group;

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
import pl.kartven.universitier.domain.model.Group;
import pl.kartven.universitier.domain.repository.GroupRepository;
import pl.kartven.universitier.infrastructure.group.dto.GroupBaseResponse;
import pl.kartven.universitier.infrastructure.group.dto.GroupForPageResponse;
import pl.kartven.universitier.infrastructure.group.dto.GroupViewResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Component
public class GroupGetUseCase implements IGroupGetUseCase {
    private final GroupRepository repository;
    private final GroupMapper mapper;

    @Override
    public Either<ApiException, Page<GroupForPageResponse>> execute(
            FilterParams filterParams, PageRequest pageRequest
    ) {
        return Option.of(executeInRepo(filterParams, pageRequest))
                .toEither((ApiException) new ServerProcessingException())
                .map(pages -> new PageImpl<>(
                        mapToGroupForPage(pages), pages.getPageable(), pages.getTotalElements()
                ));
    }

    private Page<Group> executeInRepo(FilterParams filterParams, PageRequest pageRequest) {
        return repository.findAllByCriteria(
                Try.of(() -> Integer.parseInt(filterParams.getPhrase())).getOrNull(),
                Try.of(() -> Group.Type.valueOf(filterParams.getPhrase()).toString()).getOrNull(),
                pageRequest
        );
    }

    private List<GroupForPageResponse> mapToGroupForPage(Page<Group> pages) {
        return pages.stream().map(mapper::mapToForPage).toList();
    }

    @Override
    public Either<ApiException, GroupViewResponse> execute(Long id) {
        return Option.ofOptional(repository.findById(id))
                .toEither((ApiException) new ResourceNotFoundException("Group not found: " + id))
                .map(mapper::mapToView);
    }

    @Override
    public Either<ApiException, List<GroupBaseResponse>> execute(SelectableParams params) {
        Either<ApiException, List<Group>> groups = null;
        if (params.getModuleId() != null && params.getAcademicYearId() != null)
            groups = Option.of(repository.findAllByModuleIdAndAYId(params.getModuleId(), params.getAcademicYearId()))
                    .map(set -> ((List<Group>) new ArrayList<>(set)))
                    .toEither(new ServerProcessingException());
        if (groups == null)
            groups = Option.of(repository.findAll())
                    .toEither(new ServerProcessingException());
        return groups.map(mapper::mapToListBase);
    }

    @Mapper(componentModel = "spring")
    public interface GroupMapper {
        GroupForPageResponse mapToForPage(Group group);

        GroupViewResponse mapToView(Group group);

        List<GroupBaseResponse> mapToListBase(List<Group> groups);
    }
}
