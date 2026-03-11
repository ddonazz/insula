package it.andrea.insula.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ImmutableResourceException extends BaseLocalizedException {
    public ImmutableResourceException(ErrorDefinition errorDefinition, Object... args) {
        super(errorDefinition, args);
    }
}

