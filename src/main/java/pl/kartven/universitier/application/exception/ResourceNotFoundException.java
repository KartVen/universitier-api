package pl.kartven.universitier.application.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ServerProcessingException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }
}