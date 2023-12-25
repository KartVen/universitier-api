package pl.kartven.universitier.application.exception;

import org.springframework.http.HttpStatus;

public interface ApiException {
    HttpStatus getStatus();
    String getMessage();
}
