package dcom.user_service.business.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserNotFound extends ResponseStatusException {
    public UserNotFound(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
