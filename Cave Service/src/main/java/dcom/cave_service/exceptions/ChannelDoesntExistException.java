package dcom.cave_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ChannelDoesntExistException extends ResponseStatusException {
    public ChannelDoesntExistException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
