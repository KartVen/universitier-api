package pl.kartven.universitier.application.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends ServerProcessingException {
    public ForbiddenException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.FORBIDDEN;
    }
}