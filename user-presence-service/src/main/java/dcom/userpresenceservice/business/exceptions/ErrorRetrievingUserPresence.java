package dcom.userpresenceservice.business.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ErrorRetrievingUserPresence extends ResponseStatusException {
    public ErrorRetrievingUserPresence(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
