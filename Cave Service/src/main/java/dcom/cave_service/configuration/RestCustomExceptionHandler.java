package dcom.cave_service.configuration;

import dcom.cave_service.exceptions.CaveNotFoundException;
import dcom.cave_service.exceptions.InvalidCaveInviteException;
import dcom.cave_service.exceptions.InvalidExpirationDateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
@Slf4j
public class RestCustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {CaveNotFoundException.class})
    public ResponseEntity<Object> handleCaveNotFoundExceptionNotFound(final CaveNotFoundException error) {
        log.error("CaveNotFoundException with status {} occurred {}", HttpStatus.NOT_FOUND, error);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error.getReason());
    }

    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException error) {
        log.error("MethodArgumentTypeMismatchException with status {} occurred {}", HttpStatus.BAD_REQUEST, error);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad Request");
    }

    @ExceptionHandler(value = {InvalidExpirationDateException.class})
    public ResponseEntity<Object> handleInvalidExpirationDateException(final InvalidExpirationDateException error) {
        log.error("InvalidExpirationDateException with status {} occurred {}", HttpStatus.BAD_REQUEST, error);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.getReason());
    }

    @ExceptionHandler(value = {InvalidCaveInviteException.class})
    public ResponseEntity<Object> handleInvalidCaveInviteException(final InvalidCaveInviteException error) {
        log.error("InvalidCaveInviteException with status {} occurred {}", HttpStatus.BAD_REQUEST, error);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.getReason());
    }
}
