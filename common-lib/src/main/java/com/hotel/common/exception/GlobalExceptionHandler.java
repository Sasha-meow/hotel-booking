package com.hotel.common.exception;

import com.hotel.common.dto.ErrorResponse;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Глобальный обработчик исключений */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException error) {
    log.warn("[WARN] NotFoundException: {}", error.getMessage());
    return buildResponse(HttpStatus.NOT_FOUND, error.getMessage());
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ErrorResponse> handleConflict(ConflictException error) {
    log.warn("[WARN] ConflictException: {}", error.getMessage());
    return buildResponse(HttpStatus.CONFLICT, error.getMessage());
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException error) {
    log.warn("[ERROR] Authentication failed: {}", error.getMessage());
    return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid login or password");
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException error) {
    log.warn("[WARN] Access denied: {}", error.getMessage());
    return buildResponse(HttpStatus.FORBIDDEN, "Access denied");
  }

  @ExceptionHandler({IllegalArgumentException.class, RuntimeException.class})
  public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException error) {
    log.warn("[ERROR] Bad request: {}", error.getMessage());
    return buildResponse(HttpStatus.BAD_REQUEST, error.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException error) {
    String errorMsg =
        error.getBindingResult().getFieldErrors().stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .findFirst()
            .orElse("Validation failed");
    log.warn("[ERROR] Validation error: {}", errorMsg);
    return buildResponse(HttpStatus.BAD_REQUEST, errorMsg);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleInvalidFormat(HttpMessageNotReadableException error) {
    log.warn("[ERROR] Invalid request format: {}", error.getMessage());
    return buildResponse(HttpStatus.BAD_REQUEST, "Invalid JSON request format");
  }

  private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
    ErrorResponse body =
        new ErrorResponse(LocalDateTime.now(), status.value(), status.getReasonPhrase(), message);
    return new ResponseEntity<>(body, status);
  }
}
