package it.andrea.insula.core.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    private ResponseEntity<ErrorResponse> buildLocalizedError(
            ErrorDefinition definition,
            Object[] args,
            HttpServletRequest request,
            HttpStatus status) {

        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(
                definition.getErrorCode(),
                args,
                definition.getDefaultMessage(),
                locale
        );

        ErrorResponse errorResponse = ErrorResponse.builder()
                .instant(Instant.now())
                .message(message)
                .status(status.value())
                .error(status.getReasonPhrase())
                .path(request.getRequestURI())
                .appCode(definition.getCode())
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildLocalizedError(ex.getErrorDefinition(), ex.getArgs(), request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceInUseException.class)
    public ResponseEntity<ErrorResponse> handleResourceInUseException(ResourceInUseException ex, HttpServletRequest request) {
        return buildLocalizedError(ex.getErrorDefinition(), ex.getArgs(), request, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex, HttpServletRequest request) {
        return buildLocalizedError(CommonErrorCodes.UNAUTHENTICATED, null, request, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        return buildLocalizedError(CommonErrorCodes.ACCESS_DENIED, null, request, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Locale locale = LocaleContextHolder.getLocale();

        String errorDetails = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                    String msg = messageSource.getMessage(error, locale);
                    return "'" + error.getField() + "': " + msg;
                })
                .collect(Collectors.joining(", "));

        ErrorDefinition valDef = CommonErrorCodes.VALIDATION_FAILED;
        String localizedBaseMessage = messageSource.getMessage(valDef.getErrorCode(), null, valDef.getDefaultMessage(), locale);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .instant(Instant.now())
                .message(localizedBaseMessage + " [" + errorDetails + "]")
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .path(request.getRequestURI())
                .appCode(valDef.getCode())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception occurred: ", ex);
        return buildLocalizedError(CommonErrorCodes.INTERNAL_SERVER_ERROR, null, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}