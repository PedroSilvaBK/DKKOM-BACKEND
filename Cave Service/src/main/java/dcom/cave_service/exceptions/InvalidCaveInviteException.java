package dcom.cave_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidCaveInviteException extends ResponseStatusException {
    public InvalidCaveInviteException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
