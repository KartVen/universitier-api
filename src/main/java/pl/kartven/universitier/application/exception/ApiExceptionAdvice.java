package pl.kartven.universitier.application.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionAdvice {
    @ExceptionHandler(ServerProcessingException.class)
    public ResponseEntity<ApiError> handleApiException(ServerProcessingException ex) {
        return new ResponseEntity<>(
                new ApiError(ex.getStatus(), ex.getMessage()),
                ex.getStatus()
        );
    }
}