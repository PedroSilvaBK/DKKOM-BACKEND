package dcom.cave_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EntityAlreadyExistsException extends ResponseStatusException {
    public EntityAlreadyExistsException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
