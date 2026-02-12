package it.andrea.insula.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends BaseLocalizedException {
    public ResourceNotFoundException(ErrorDefinition errorDefinition, Object... args) {
        super(errorDefinition, args);
    }
}