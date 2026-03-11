package it.andrea.insula.core.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_CONTENT;

@ResponseStatus(UNPROCESSABLE_CONTENT)
public class BusinessRuleException extends BaseLocalizedException {
    public BusinessRuleException(ErrorDefinition errorDefinition, Object... args) {
        super(errorDefinition, args);
    }
}
