package account.common.exception;

import account.auth.exception.custom.*;
import account.payment.exception.EmployeePeriodDuplicateException;
import account.payment.exception.InvalidPaymentPeriodException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.Date;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ResourceNotFound.class})
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFound ex, HttpServletRequest request) {
        log.debug("Resource not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .message(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .path(request.getRequestURI())
                        .timestamp(new Date())
                        .build());
    }

    /**
     * General exception handler for all bad requests.
     *
     * @param ex      The exception that occurred.
     * @param request The request that caused the exception.
     * @return The error response.
     */
    @ExceptionHandler({
            UserExistException.class, BreachPasswordException.class, PasswordMatchException.class, EmployeePeriodDuplicateException.class,
            InvalidPaymentPeriodException.class, ForbiddenOperationException.class, InvalidRoleOperationException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(RuntimeException ex, HttpServletRequest request) {
        log.debug("Handling BadRequestException in Global Exception Handler: {}", ex.getMessage());
        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .timestamp(new Date())
                        .build());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HandlerMethodValidationException.class})
    public ResponseEntity<ErrorResponse> handleValidationExceptions(Exception ex, WebRequest request) {
        String errorMessage;

        if (ex instanceof MethodArgumentNotValidException maex) {
            errorMessage = maex.getBindingResult().getFieldErrors().stream()
                    .findFirst()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .orElse("Validation failed");
        } else if (ex instanceof HandlerMethodValidationException hmvex) {
            errorMessage = hmvex.getAllValidationResults().stream()
                    .flatMap(vr -> vr.getResolvableErrors().stream())
                    .findFirst()
                    .map(error -> {
                        try {
                            return error.getDefaultMessage();
                        } catch (Exception e) {
                            return "Validation failure";
                        }
                    })
                    .orElse("Validation failure");
        } else {
            errorMessage = "Validation failed";
        }

        return ResponseEntity.badRequest()
                .body(buildErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        HttpStatus.BAD_REQUEST.getReasonPhrase(),
                        errorMessage,
                        getRequestUri(request))
                );
    }

    /* Helper methods */

    private ErrorResponse buildErrorResponse(int status, String error, String message, String path) {
        return new ErrorResponse(new Date(), status, error, message, path);
    }

    private String getRequestUri(WebRequest request) {
        return ((ServletWebRequest) request).getRequest().getRequestURI();
    }
}