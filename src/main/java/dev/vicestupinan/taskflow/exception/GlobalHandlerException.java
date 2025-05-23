package dev.vicestupinan.taskflow.exception;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccesDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(buildErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage()));
    }

    private Map<String, Object> buildErrorResponse(HttpStatus status, String message) {
        return Map.of(
                "timestamp", LocalDateTime.now(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message
        );
    }
}
