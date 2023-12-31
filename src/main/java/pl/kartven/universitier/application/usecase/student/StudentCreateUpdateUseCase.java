package pl.kartven.universitier.application.usecase.student;

import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.exception.ResourceNotFoundException;
import pl.kartven.universitier.application.exception.ServerProcessingException;
import pl.kartven.universitier.application.util.JpaHelper;
import pl.kartven.universitier.domain.model.AcademicYear;
import pl.kartven.universitier.domain.model.Programme;
import pl.kartven.universitier.domain.model.Student;
import pl.kartven.universitier.domain.model.User;
import pl.kartven.universitier.domain.repository.*;
import pl.kartven.universitier.infrastructure.common.dto.AddEditResponse;
import pl.kartven.universitier.infrastructure.student.dto.StudentAddEditRequest;

@AllArgsConstructor
@Component
public class StudentCreateUpdateUseCase implements IStudentCreateUpdateUseCase {
    private final StudentRepository repository;
    private final StudentMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final AcademicYearRepository academicYearRepository;
    private final ConnectionRepository connectionRepository;
    private final ProgrammeRepository programmeRepository;

    @Override
    public Either<ApiException, AddEditResponse> execute(StudentAddEditRequest request) {
        var programmeIds = request.getProgrammes().stream()
                .map(StudentAddEditRequest.Programme::getProgrammeId).toList();
        var programmeEither = JpaHelper.getEntitiesSets(
                programmeRepository::findDistinctByIdIn, programmeIds,
                Programme::getId, "Programmes"
        );
        if (programmeEither.isLeft()) return Either.left(programmeEither.getLeft());

        var acaYearsIds = request.getProgrammes().stream()
                .map(StudentAddEditRequest.Programme::getAcademicYearId).toList();
        var acaYearsEither = JpaHelper.getEntitiesSets(
                academicYearRepository::findDistinctByIdIn, acaYearsIds,
                AcademicYear::getId, "Academic years"
        );
        if (acaYearsEither.isLeft()) return Either.left(acaYearsEither.getLeft());
        var progConsEither = Option.of(connectionRepository.findAllByProgrammeIdInAndAYIdIn(programmeIds, acaYearsIds))
                .toEither((ApiException) new ServerProcessingException());
        if (progConsEither.isLeft()) return Either.left(progConsEither.getLeft());
        var student = mapper.map(request, passwordEncoder);
        student.setConnections(progConsEither.get());
        return Try.of(() -> repository.save(student))
                .toEither()
                .mapLeft(th -> (ApiException) new ServerProcessingException(th.getMessage()))
                .map(studEntity -> new AddEditResponse(studEntity.getId().toString(),
                        String.format("%s %s", studEntity.getFirstName(), studEntity.getLastName())
                ));
    }

    @Override
    public Either<ApiException, Void> execute(Long id, StudentAddEditRequest request) {
        var studentEither = Option.ofOptional(repository.findByIdWithU(id))
                .toEither((ApiException) new ResourceNotFoundException("Student not found: " + id));
        if (studentEither.isLeft()) return Either.left(studentEither.getLeft());
        return Try.of(() -> repository.save(mapper.update(studentEither.get(), request)))
                .toEither()
                .mapLeft(th -> (ApiException) new ServerProcessingException(th.getMessage()))
                .map(unused -> null);
    }

    @Mapper(componentModel = "spring")
    public interface StudentMapper {
        default Student map(StudentAddEditRequest request, PasswordEncoder passwordEncoder) {
            var user = new User(passwordEncoder.encode(request.getPassword()), User.Role.STUDENT);
            return new Student(request.getFirstName(), request.getLastName(), user);
        }

        default Student update(Student entity, StudentAddEditRequest request) {
            entity.setFirstName(request.getFirstName());
            entity.setLastName(request.getLastName());
            return entity;
        }
    }
}
