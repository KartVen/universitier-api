package pl.kartven.universitier.application.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends ServerProcessingException {
    public UnauthorizedException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.UNAUTHORIZED;
    }
}