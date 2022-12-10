package ru.practicum.shareit.exceptions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
<<<<<<< HEAD
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
=======
>>>>>>> 4f16f1bf88eed9c7fa247ad0c502c2e149be4d77

@Slf4j
@RestControllerAdvice
public class ExceptionsHandler {

    @ExceptionHandler(NotFoundValidationException.class)
    public ResponseEntity<ErrorMessage> notFoundValidationException(NotFoundValidationException exception) {
        log.warn(exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(IllegalRequestException.class)
    public ResponseEntity<ErrorMessage> illegalRequestException(IllegalRequestException exception) {
        log.warn(exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorMessage> conflictException(ConflictException exception) {
        log.warn(exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @Getter
    @RequiredArgsConstructor
    static class ErrorMessage {
<<<<<<< HEAD
        private final String error;
=======
        private final String message;
>>>>>>> 4f16f1bf88eed9c7fa247ad0c502c2e149be4d77
    }
}

