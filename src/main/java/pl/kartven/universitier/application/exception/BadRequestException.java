package pl.kartven.universitier.application.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ServerProcessingException {
    public BadRequestException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}