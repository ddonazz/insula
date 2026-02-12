package it.andrea.insula.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceInUseException extends BaseLocalizedException {
    public ResourceInUseException(ErrorDefinition errorDefinition, Object... args) {
        super(errorDefinition, args);
    }
}
