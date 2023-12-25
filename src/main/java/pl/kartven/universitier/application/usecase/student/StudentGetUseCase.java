package pl.kartven.universitier.application.usecase.student;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.exception.ServerProcessingException;
import pl.kartven.universitier.application.util.FilterParams;
import pl.kartven.universitier.domain.model.Student;
import pl.kartven.universitier.domain.repository.StudentRepository;
import pl.kartven.universitier.infrastructure.student.dto.StudentForPageResponse;

import java.util.List;

@AllArgsConstructor
@Component
public class StudentGetUseCase {
    private final StudentRepository repository;
    private final StudentMapper mapper;

    public Either<ApiException, Page<StudentForPageResponse>> execute(
            FilterParams filterParams, PageRequest pageRequest
    ) {
        return Option.of(executeInRepo(filterParams, pageRequest))
                .toEither((ApiException) new ServerProcessingException())
                .map(pages -> new PageImpl<>(
                        mapToStudentForPage(pages), pages.getPageable(), pages.getTotalElements()
                ));
    }

    private List<StudentForPageResponse> mapToStudentForPage(Page<Student> pages) {
        return pages.stream().map(mapper::map).toList();
    }

    private Page<Student> executeInRepo(FilterParams filterParams, PageRequest pageRequest) {
        return repository.findAllByCriteria(
                filterParams.getPhrase(),
                filterParams.getPhrase(),
                filterParams.getPhrase(),
                pageRequest
        );
    }

    @Mapper(componentModel = "spring")
    public interface StudentMapper {
        StudentForPageResponse map(Student student);
    }
}
