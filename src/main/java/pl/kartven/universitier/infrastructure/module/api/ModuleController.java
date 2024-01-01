package pl.kartven.universitier.infrastructure.module.api;

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
import pl.kartven.universitier.application.usecase.module.IModuleCreateUpdateUseCase;
import pl.kartven.universitier.application.usecase.module.IModuleDeleteUseCase;
import pl.kartven.universitier.application.usecase.module.IModuleGetUseCase;
import pl.kartven.universitier.application.util.FilterParams;
import pl.kartven.universitier.application.util.PredefinedApiResponse;
import pl.kartven.universitier.application.util.RestErrorHandler;
import pl.kartven.universitier.infrastructure.module.dto.ModuleAddEditRequest;
import pl.kartven.universitier.infrastructure.module.dto.ModuleBaseResponse;
import pl.kartven.universitier.infrastructure.module.dto.ModuleViewResponse;

import java.net.URI;
import java.util.Objects;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
@Tag(name = "Modules")
public class ModuleController implements RestErrorHandler {
    private final IModuleGetUseCase getUseCase;
    private final IModuleCreateUpdateUseCase createUpdateUseCase;
    private final IModuleDeleteUseCase deleteUseCase;

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
            content = @Content(schema = @Schema(implementation = ModuleViewResponse.class)))
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
    public ResponseEntity<?> create(@RequestBody ModuleAddEditRequest request) {
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
            @RequestBody ModuleAddEditRequest request
    ) {
        return createUpdateUseCase.execute(id, request)
                .fold(this::handleError, s -> ResponseEntity.noContent().build());
    }

    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = ModuleBaseResponse.class)))
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
