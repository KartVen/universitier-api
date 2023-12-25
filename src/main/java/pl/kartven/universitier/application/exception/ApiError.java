package pl.kartven.universitier.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class ApiError {
    private final HttpStatus status;
    private final String message;

    public static ApiError map(ApiException exception){
        return new ApiError(exception.getStatus(), exception.getMessage());
    }
}
