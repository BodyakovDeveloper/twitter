package koval.proxyseller.twitter.exception

import groovy.util.logging.Slf4j
import koval.proxyseller.twitter.monitoring.MetricsService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

import java.time.LocalDateTime

@Slf4j
@RestControllerAdvice
class GlobalExceptionHandler {
    private final MetricsService metricsService

    GlobalExceptionHandler(MetricsService metricsService) {
        this.metricsService = metricsService
    }

    @ExceptionHandler(EntityNotFoundException.class)
    ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("Entity not found: ${ex.message}")
        metricsService.incrementEntityNotFoundErrors()
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Entity Not Found",
                ex.message
        )
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(RegistrationException.class)
    ResponseEntity<ErrorResponse> handleRegistrationException(RegistrationException ex) {
        log.error("Registration error: ${ex.message}")
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Registration Failed",
                ex.message
        )
        return new ResponseEntity<>(error, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(DuplicateEntityException.class)
    ResponseEntity<ErrorResponse> handleDuplicateEntityException(DuplicateEntityException ex) {
        log.error("Duplicate entity: ${ex.message}")
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Duplicate Entity",
                ex.message
        )
        return new ResponseEntity<>(error, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(InvalidOperationException.class)
    ResponseEntity<ErrorResponse> handleInvalidOperationException(InvalidOperationException ex) {
        log.error("Invalid operation: ${ex.message}")
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Operation",
                ex.message
        )
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        log.error("Authentication error: ${ex.message}")
        metricsService.incrementAuthenticationErrors()
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Authentication Failed",
                ex.message
        )
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(AuthorizationException.class)
    ResponseEntity<ErrorResponse> handleAuthorizationException(AuthorizationException ex) {
        log.error("Authorization error: ${ex.message}")
        metricsService.incrementAuthorizationErrors()
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "Access Denied",
                ex.message
        )
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(ResourceOwnershipException.class)
    ResponseEntity<ErrorResponse> handleResourceOwnershipException(ResourceOwnershipException ex) {
        log.error("Resource ownership error: ${ex.message}")
        metricsService.incrementAuthorizationErrors()
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "Resource Ownership Violation",
                ex.message
        )
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(ValidationException.class)
    ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        log.error("Validation error: ${ex.message}")
        metricsService.incrementValidationErrors()
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                ex.message
        )
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("Validation error: ${ex.bindingResult?.allErrors?.size() ?: 0} validation errors")
        StringBuilder errors = new StringBuilder()
        ex.bindingResult.allErrors.each { error ->
            String fieldName = ((FieldError) error).field
            String errorMessage = error.defaultMessage
            errors.append("${fieldName}: ${errorMessage}; ")
        }
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                errors.toString()
        )
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        log.error("Bad credentials: ${ex.message}")
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Authentication Failed",
                "Invalid username or password"
        )
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Access denied: ${ex.message}")
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "Access Denied",
                "You don't have permission to access this resource"
        )
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument: ${ex.message}")
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Argument",
                ex.message
        )
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error: ${ex.message}", ex)
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred"
        )
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}

class ErrorResponse {
    LocalDateTime timestamp
    int status
    String error
    String message

    ErrorResponse(LocalDateTime timestamp, int status, String error, String message) {
        this.timestamp = timestamp
        this.status = status
        this.error = error
        this.message = message
    }
}

