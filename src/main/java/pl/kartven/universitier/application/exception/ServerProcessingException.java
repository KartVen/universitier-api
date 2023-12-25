package pl.kartven.universitier.application.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ServerProcessingException extends RuntimeException implements ApiException {
    public ServerProcessingException(String message) {
        super(message);
    }

    public ServerProcessingException() {
        super("Internal Server Error");
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
