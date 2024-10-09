package dcom.cave_service.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CaveNotFoundException extends ResponseStatusException {
    public CaveNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
