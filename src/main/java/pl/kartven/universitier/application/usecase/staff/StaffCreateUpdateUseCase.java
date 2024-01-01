package pl.kartven.universitier.application.usecase.staff;

import io.vavr.Predicates;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.exception.BadRequestException;
import pl.kartven.universitier.application.exception.ServerProcessingException;
import pl.kartven.universitier.application.util.JpaHelper;
import pl.kartven.universitier.domain.model.Module;
import pl.kartven.universitier.domain.model.*;
import pl.kartven.universitier.domain.repository.*;
import pl.kartven.universitier.infrastructure.common.dto.AddEditResponse;
import pl.kartven.universitier.infrastructure.staff.dto.StaffAddEditRequest;

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
    private final FacultyRepository facultyRepository;

    @Override
    public Either<ApiException, AddEditResponse> execute(StaffAddEditRequest request) {
        var staff = mapper.map(request);
        staff.setUser(new User(
                passwordEncoder.encode(request.getPassword()), User.Role.valueOf(request.getRole().toString())
        ));
        Either<ApiException, Staff> staffEither = Match(request.getRole()).of(
                Case($(Predicates.is(StaffAddEditRequest.RoleEDto.NON_ACADEMIC_STAFF)), () -> {
                    staff.setWorks(request.getWorks().stream()
                            .map(work -> new Work(
                                    Work.Type.NON_ACADEMIC,
                                    work.getPositionName(),
                                    Work.Status.valueOf(work.getStatus().toString()),
                                    null
                            )).collect(Collectors.toSet()));
                    return Either.right(staff);
                }),
                Case($(Predicates.is(StaffAddEditRequest.RoleEDto.ACADEMIC_STAFF)), () -> {
                    var facultiesEither = JpaHelper.getEntitiesSets(
                        facultyRepository::findDistinctByIdIn,
                            request.getWorks().stream().map(StaffAddEditRequest.WorkDto::getFacultyId).toList(),
                            Faculty::getId, "Faculties"
                    );
                    if (facultiesEither.isLeft()) return Either.left(facultiesEither.getLeft());
                    var facultyMap = facultiesEither.get().stream()
                            .collect(Collectors.toMap(Faculty::getId, Function.identity()));
                    staff.setWorks(request.getWorks().stream()
                            .map(work -> new Work(
                                    Work.Type.ACADEMIC,
                                    work.getPositionName(),
                                    Work.Status.valueOf(work.getStatus().toString()),
                                    facultyMap.get(work.getFacultyId())
                            )).collect(Collectors.toSet()));
                    return Either.right(staff);
                }),
                Case($(Predicates.is(StaffAddEditRequest.RoleEDto.LECTURER)), () -> {
                    var groupsEither = JpaHelper.getEntitiesSets(
                            groupRepository::findDistinctByIdIn, request.getGroups().stream()
                                    .map(StaffAddEditRequest.GroupDto::getGroupId).toList(),
                            Group::getId, "Groups"
                    );
                    if (groupsEither.isLeft()) return Either.left(groupsEither.getLeft());
                    var groupMap = groupsEither.get().stream()
                            .collect(Collectors.toMap(Group::getId, Function.identity()));

                    var acaYearsEither = JpaHelper.getEntitiesSets(
                            academicYearRepository::findDistinctByIdIn,
                            request.getGroups().stream().map(StaffAddEditRequest.GroupDto::getAcademicYearId).toList(),
                            AcademicYear::getId, "Academic years"
                    );
                    var acaYearMap = acaYearsEither.get().stream()
                            .collect(Collectors.toMap(AcademicYear::getId, Function.identity()));
                    if (acaYearsEither.isLeft()) return Either.left(acaYearsEither.getLeft());

                    var modulesEither = JpaHelper.getEntitiesSets(
                            moduleRepository::findDistinctByIdIn,
                            request.getGroups().stream().map(StaffAddEditRequest.GroupDto::getAcademicYearId).toList(),
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
                .map(staffEnt -> new AddEditResponse(staffEnt.getKeyId(),
                        String.format("%s %s", staffEnt.getFirstName(), staffEnt.getLastName())
                ));
    }

    @Override
    public Either<ApiException, Void> execute(Long id, StaffAddEditRequest request) {
        return Either.right(null);
    }

    @Mapper(componentModel = "spring")
    public interface StaffMapper {

        @Mapping(target = "id", ignore = true)
        @Mapping(target = "user", ignore = true)
        @Mapping(target = "keyId", source = "username")
        @Mapping(target = "connections", ignore = true)
        @Mapping(target = "works", ignore = true)
        @Mapping(target = "addressStreet", source = "address.street")
        @Mapping(target = "addressHome", source = "address.home")
        @Mapping(target = "addressZipCode", source = "address.zipCode")
        @Mapping(target = "addressCity", source = "address.city")
        Staff map(StaffAddEditRequest request);

        default Staff update(Staff entity, StaffAddEditRequest request) {
            entity.setFirstName(request.getFirstName());
            entity.setLastName(request.getLastName());
            entity.setAddressStreet(request.getAddress().getStreet());
            entity.setAddressHome(request.getAddress().getHome());
            entity.setAddressZipCode(request.getAddress().getZipCode());
            entity.setAddressCity(request.getAddress().getCity());
            return entity;
        }
    }
}
