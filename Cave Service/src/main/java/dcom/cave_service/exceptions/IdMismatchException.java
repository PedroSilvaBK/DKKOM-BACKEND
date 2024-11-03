package dcom.cave_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class IdMismatchException extends ResponseStatusException {
    public IdMismatchException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
