package pl.kartven.universitier.infrastructure.faculty.api;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.kartven.universitier.application.exception.ApiError;
import pl.kartven.universitier.application.usecase.faculty.IFacultyCreateUpdateUseCase;
import pl.kartven.universitier.application.usecase.faculty.IFacultyDeleteUseCase;
import pl.kartven.universitier.application.usecase.faculty.IFacultyGetUseCase;
import pl.kartven.universitier.application.util.FilterParams;
import pl.kartven.universitier.application.util.PredefinedApiResponse;
import pl.kartven.universitier.application.util.RestErrorHandler;
import pl.kartven.universitier.infrastructure.faculty.dto.FacultyAddEditRequest;
import pl.kartven.universitier.infrastructure.faculty.dto.FacultyBaseResponse;
import pl.kartven.universitier.infrastructure.faculty.dto.FacultyViewResponse;

import java.net.URI;
import java.util.Objects;

@RestController
@RequestMapping("/api/faculties")
@RequiredArgsConstructor
@Tag(name = "Faculties")
@PredefinedApiResponse
public class FacultyController implements RestErrorHandler {
    private final IFacultyGetUseCase getUseCase;
    private final IFacultyCreateUpdateUseCase createUpdateUseCase;
    private final IFacultyDeleteUseCase deleteUseCase;


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
            content = @Content(schema = @Schema(implementation = FacultyViewResponse.class)))
    @PredefinedApiResponse.NotFound
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return getUseCase.execute(id)
                .fold(this::handleError, ResponseEntity::ok);
    }

    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema()))
    @PredefinedApiResponse.BadRequest
    @PredefinedApiResponse.Created
    @PreAuthorize("isAuthenticated() && hasRole('ADMINISTRATOR')")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody FacultyAddEditRequest request) {
        return createUpdateUseCase.execute(request)
                .fold(this::handleError, s -> ResponseEntity.created(URI.create(s.getId())).body(s));
    }

    @PredefinedApiResponse.NoContent
    @PredefinedApiResponse.NotFound
    @PredefinedApiResponse.BadRequest
    @PreAuthorize("isAuthenticated() && hasRole('ADMINISTRATOR')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody FacultyAddEditRequest request
    ) {
        return createUpdateUseCase.execute(id, request)
                .fold(this::handleError, s -> ResponseEntity.noContent().build());
    }


    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = FacultyBaseResponse.class)))
    @GetMapping("/selectable")
    public ResponseEntity<?> getSelectable() {
        return getUseCase.execute()
                .fold(this::handleError, ResponseEntity::ok);
    }


    @PredefinedApiResponse.NotFound
    @PredefinedApiResponse.NoContent
    @PreAuthorize("isAuthenticated() && hasRole('ADMINISTRATOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return deleteUseCase.execute(id)
                .fold(this::handleError, s -> ResponseEntity.noContent().build());
    }
}
