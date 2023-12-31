package pl.kartven.universitier.application.util;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import pl.kartven.universitier.application.exception.ApiError;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.CLASS;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({METHOD, TYPE, ANNOTATION_TYPE})
@ApiResponse(responseCode = "500", description = "Internal Server Error",
        content = @Content(schema = @Schema(implementation = ApiError.class))
)
public @interface PredefinedApiResponse {

    @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @Target({METHOD, TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @interface BadRequest {
    }

    @ApiResponse(responseCode = "404", description = "Not found",
            content = @Content(schema = @Schema(implementation = ApiError.class)))
    @Target({METHOD, TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @interface NotFound {
    }

    @Target({METHOD, TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @ApiResponse(responseCode = "201", description = "Created")
    @interface Created {
    }

    @Target({METHOD, TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @ApiResponse(responseCode = "204", description = "No content")
    @interface NoContent {
    }
}
