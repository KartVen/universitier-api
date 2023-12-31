package pl.kartven.universitier.application.usecase.staff;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.exception.BadRequestException;
import pl.kartven.universitier.application.exception.ServerProcessingException;
import pl.kartven.universitier.application.util.JpaHelper;
import pl.kartven.universitier.domain.model.*;
import pl.kartven.universitier.domain.model.Module;
import pl.kartven.universitier.domain.repository.*;
import pl.kartven.universitier.infrastructure.common.dto.AddEditResponse;
import pl.kartven.universitier.infrastructure.staff.dto.StaffAddEditRequest;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.vavr.API.*;

@AllArgsConstructor
@Component
public class StaffCreateUpdateUseCase implements IStaffCreateUpdateUseCase {
    private final StaffRepository repository;
    private final StaffMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final ModuleRepository moduleRepository;
    private final GroupRepository groupRepository;
    private final AcademicYearRepository academicYearRepository;

    @Override
    public Either<ApiException, AddEditResponse> execute(StaffAddEditRequest request) {
        var staff = mapper.map(request, passwordEncoder);
        Either<ApiException, Staff> staffEither = Match(request.getRole()).of(
                Case($(role -> role.equals(User.Role.STAFF)), () -> {
                    staff.setWorks(request.getWorks().stream()
                            .map(work -> new Work(work.getPosition(), work.getStatus()))
                            .collect(Collectors.toSet()));
                    return Either.right(staff);
                }),
                Case($(role -> role.equals(User.Role.LECTURER)), () -> {
                    var groupsEither = JpaHelper.getEntitiesSets(
                            groupRepository::findDistinctByIdIn, request.getGroups().stream()
                                    .map(StaffAddEditRequest.Group::getGroupId).toList(),
                            Group::getId, "Groups"
                    );
                    if (groupsEither.isLeft()) return Either.left(groupsEither.getLeft());
                    var groupMap = groupsEither.get().stream()
                            .collect(Collectors.toMap(Group::getId, Function.identity()));

                    var acaYearsEither = JpaHelper.getEntitiesSets(
                            academicYearRepository::findDistinctByIdIn,
                            request.getGroups().stream().map(StaffAddEditRequest.Group::getAcademicYearId).toList(),
                            AcademicYear::getId, "Academic years"
                    );
                    var acaYearMap = acaYearsEither.get().stream()
                            .collect(Collectors.toMap(AcademicYear::getId, Function.identity()));
                    if (acaYearsEither.isLeft()) return Either.left(acaYearsEither.getLeft());

                    var modulesEither = JpaHelper.getEntitiesSets(
                            moduleRepository::findDistinctByIdIn,
                            request.getGroups().stream().map(StaffAddEditRequest.Group::getAcademicYearId).toList(),
                            Module::getId, "Academic years"
                    );
                    var moduleMap = modulesEither.get().stream()
                            .collect(Collectors.toMap(Module::getId, Function.identity()));
                    if (modulesEither.isLeft()) return Either.left(modulesEither.getLeft());

                    staff.setConnections(request.getGroups().stream().map(group -> new Connection(
                        moduleMap.get(group.getModuleId()),
                        acaYearMap.get(group.getAcademicYearId()),
                        groupMap.get(group.getGroupId())
                    )).collect(Collectors.toSet()));
                    return Either.right(staff);
                }),
                Case($(), () -> Either.left(new BadRequestException("Invalid staff role")))
        );
        if (staffEither.isLeft()) return Either.left(staffEither.getLeft());
        return Try.of(() -> repository.save(staffEither.get()))
                .toEither()
                .mapLeft(th -> (ApiException) new ServerProcessingException(th.getMessage()))
                .map(staffEnt -> new AddEditResponse(staffEnt.getReferenceId(),
                        String.format("%s %s", staffEnt.getFirstName(), staffEnt.getLastName())
                ));
    }

    @Override
    public Either<ApiException, Void> execute(Long id, StaffAddEditRequest request) {
        return Either.right(null);
    }


    private <T> Either<ApiException, Set<T>> getEntitiesSets(
            Function<Collection<Long>, Set<T>> repoFindFun,
            Collection<Long> ids,
            Function<T, Long> getIdFun,
            String notFoundExceptionName
    ) {
        return Either.right(null);
    }

    @Mapper(componentModel = "spring")
    public interface StaffMapper {
        default Staff map(StaffAddEditRequest request, PasswordEncoder passwordEncoder) {
            var user = new User(passwordEncoder.encode(request.getPassword()), request.getRole());
            return new Staff(
                    request.getUsername(), request.getFirstName(), request.getLastName(), user
            );
        }

        default Staff update(Staff entity, StaffAddEditRequest request) {
            entity.setFirstName(request.getFirstName());
            entity.setLastName(request.getLastName());
            entity.setAddressCity(request.getAddress().getCity());
            entity.setAddressStreet(request.getAddress().getStreet());
            entity.setAddressPostDist(request.getAddress().getPostDist());
            entity.setAddressZipCode(request.getAddress().getZipCode());
            return entity;
        }
    }
}
