package it.andrea.insula.core.exception;

import lombok.Getter;

@Getter
public abstract class BaseLocalizedException extends RuntimeException {

    private final ErrorDefinition errorDefinition;
    private final Object[] args;

    public BaseLocalizedException(ErrorDefinition errorDefinition, Object... args) {
        super(errorDefinition.getErrorCode());
        this.errorDefinition = errorDefinition;
        this.args = args;
    }

    public String getErrorCode() {
        return this.errorDefinition.getErrorCode();
    }

    public int getCode() {
        return this.errorDefinition.getCode();
    }

    public String getDefaultMessage() {
        return this.errorDefinition.getDefaultMessage();
    }

}
