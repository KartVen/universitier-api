package pl.kartven.universitier.application.usecase.course;

import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import pl.kartven.universitier.application.exception.ApiException;
import pl.kartven.universitier.application.exception.ResourceNotFoundException;
import pl.kartven.universitier.application.exception.ServerProcessingException;
import pl.kartven.universitier.application.util.FilterParams;
import pl.kartven.universitier.domain.model.Course;
import pl.kartven.universitier.domain.repository.AcademicYearRepository;
import pl.kartven.universitier.domain.repository.CourseRepository;
import pl.kartven.universitier.infrastructure.course.dto.CourseForPageResponse;
import pl.kartven.universitier.infrastructure.course.dto.CourseViewResponse;

import java.util.List;

@AllArgsConstructor
@Component
public class CourseGetUseCase implements ICourseGetUseCase {
    private final CourseRepository repository;
    private final CourseMapper mapper;

    @Override
    public Either<ApiException, Page<CourseForPageResponse>> execute(
            FilterParams filterParams, PageRequest pageRequest
    ) {
        return Option.of(executeInRepo(filterParams, pageRequest))
                .toEither((ApiException) new ServerProcessingException())
                .map(pages -> new PageImpl<>(
                        mapToCourseForPage(pages), pages.getPageable(), pages.getTotalElements()
                ));
    }

    private List<CourseForPageResponse> mapToCourseForPage(Page<Course> pages) {
        return pages.stream().map(mapper::mapToForPage).toList();
    }

    private Page<Course> executeInRepo(FilterParams filterParams, PageRequest pageRequest) {
        return repository.findAllByCriteria(
                filterParams.getPhrase(),
                filterParams.getPhrase(),
                pageRequest
        );
    }

    @Override
    public Either<ApiException, CourseViewResponse> execute(Long id) {
        return Option.ofOptional(repository.findByIdWithFAndAY(id))
                .toEither((ApiException) new ResourceNotFoundException("Course not found: " + id))
                .map(mapper::mapToView);
    }

    @Mapper(componentModel = "spring")
    public interface CourseMapper {
        @Mapping(target = "facultyShortName", source = "faculty.shortname")
        CourseForPageResponse mapToForPage(Course course);

        CourseViewResponse mapToView(Course course);
    }
}
