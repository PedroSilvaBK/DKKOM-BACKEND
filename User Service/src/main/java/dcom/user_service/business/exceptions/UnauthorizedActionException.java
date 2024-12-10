package dcom.user_service.business.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnauthorizedActionException extends ResponseStatusException {
    public UnauthorizedActionException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
