package pl.kartven.universitier.application.util;

import org.springframework.http.ResponseEntity;
import pl.kartven.universitier.application.exception.ApiError;
import pl.kartven.universitier.application.exception.ApiException;

public interface RestErrorHandler {
    default ResponseEntity<ApiError> handleError(ApiException error) {
        return ResponseEntity.status(error.getStatus()).body(ApiError.map(error));
    }
}
