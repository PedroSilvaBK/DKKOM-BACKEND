package dcom.cave_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidExpirationDateException extends ResponseStatusException {
    public InvalidExpirationDateException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
