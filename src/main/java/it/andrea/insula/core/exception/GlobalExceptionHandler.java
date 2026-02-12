package it.andrea.insula.core.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(BaseLocalizedException.class)
    public ResponseEntity<ErrorResponse> handleLocalizedException(BaseLocalizedException ex, HttpServletRequest request) {
        String message = resolveMessage(ex);

        ResponseStatus annotation = ex.getClass().getAnnotation(ResponseStatus.class);
        HttpStatus status = (annotation != null) ? annotation.value() : HttpStatus.INTERNAL_SERVER_ERROR;

        ErrorResponse errorResponse = ErrorResponse.builder()
                .instant(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .appCode(ex.getCode())
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }

    private String resolveMessage(BaseLocalizedException ex) {
        try {
            return messageSource.getMessage(
                    ex.getErrorCode(),
                    ex.getArgs(),
                    ex.getDefaultMessage(),
                    LocaleContextHolder.getLocale()
            );
        } catch (Exception e) {
            return ex.getDefaultMessage();
        }
    }

}