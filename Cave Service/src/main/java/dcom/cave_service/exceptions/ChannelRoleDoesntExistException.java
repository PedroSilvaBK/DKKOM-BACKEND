package dcom.cave_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ChannelRoleDoesntExistException extends ResponseStatusException {
    public ChannelRoleDoesntExistException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
