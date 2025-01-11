package dcom.messaging_service.business;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CannotSendMessageException extends ResponseStatusException {
    public CannotSendMessageException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
