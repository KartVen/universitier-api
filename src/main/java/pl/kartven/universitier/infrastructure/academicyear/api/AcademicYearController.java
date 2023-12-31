package pl.kartven.universitier.infrastructure.academicyear.api;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kartven.universitier.application.usecase.academicyear.IAcademicYearCreateUpdateUseCase;
import pl.kartven.universitier.application.usecase.academicyear.IAcademicYearDeleteUseCase;
import pl.kartven.universitier.application.usecase.academicyear.IAcademicYearGetUseCase;
import pl.kartven.universitier.application.util.FilterParams;
import pl.kartven.universitier.application.util.PredefinedApiResponse;
import pl.kartven.universitier.application.util.RestErrorHandler;
import pl.kartven.universitier.infrastructure.academicyear.dto.AcademicYearAddEditRequest;
import pl.kartven.universitier.infrastructure.academicyear.dto.AcademicYearBaseResponse;
import pl.kartven.universitier.infrastructure.academicyear.dto.AcademicYearViewResponse;
import pl.kartven.universitier.infrastructure.faculty.dto.FacultyBaseResponse;
import pl.kartven.universitier.infrastructure.faculty.dto.FacultyViewResponse;

import java.net.URI;
import java.util.Objects;

@RestController
@RequestMapping("/api/academic-years")
@RequiredArgsConstructor
@Tag(name = "AcademicYears")
public class AcademicYearController implements RestErrorHandler {
    private final IAcademicYearGetUseCase getUseCase;
    private final IAcademicYearCreateUpdateUseCase createUpdateUseCase;
    private final IAcademicYearDeleteUseCase deleteUseCase;

    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = Page.class)))
    @PredefinedApiResponse.BadRequest
    @GetMapping
    public ResponseEntity<?> get(
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(value = "sort", defaultValue = "id") String sort,
            @RequestParam(value = "sort_direction", defaultValue = "ASC") Sort.Direction sortDirection,
            @RequestParam(value = "filter_params", required = false) FilterParams filterParams
    ) {
        return getUseCase.execute(
                        Objects.isNull(filterParams) ? new FilterParams() : filterParams,
                        PageRequest.of(page, size, sortDirection, sort)
                )
                .fold(this::handleError, ResponseEntity::ok);
    }

    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = AcademicYearViewResponse.class)))
    @PredefinedApiResponse.NotFound
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return getUseCase.execute(id)
                .fold(this::handleError, ResponseEntity::ok);
    }

    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema()))
    @PredefinedApiResponse.BadRequest
    @PredefinedApiResponse.Created
    @PostMapping
    public ResponseEntity<?> create(@RequestBody AcademicYearAddEditRequest request) {
        return createUpdateUseCase.execute(request)
                .fold(this::handleError, s -> ResponseEntity.created(URI.create(s.getId())).body(s));
    }

    @PredefinedApiResponse.NoContent
    @PredefinedApiResponse.NotFound
    @PredefinedApiResponse.BadRequest
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody AcademicYearAddEditRequest request
    ) {
        return createUpdateUseCase.execute(id, request)
                .fold(this::handleError, s -> ResponseEntity.noContent().build());
    }

    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = AcademicYearBaseResponse.class)))
    @GetMapping("/selectable")
    public ResponseEntity<?> getSelectable() {
        return getUseCase.execute()
                .fold(this::handleError, ResponseEntity::ok);
    }

    @PredefinedApiResponse.NotFound
    @PredefinedApiResponse.NoContent
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return deleteUseCase.execute(id)
                .fold(this::handleError, s -> ResponseEntity.noContent().build());
    }
}
