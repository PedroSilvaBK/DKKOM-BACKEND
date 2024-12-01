package dcom.userpresenceservice.configuration;

import dcom.userpresenceservice.business.exceptions.ErrorRetrievingUserPresence;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
@Slf4j
public class RestCustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {ErrorRetrievingUserPresence.class})
    public ResponseEntity<Object> handleErrorRetrievingUserPresence(final ErrorRetrievingUserPresence error) {
        log.error("ErrorRetrievingUserPresence with status {} occurred", HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error.getReason());
    }
}
