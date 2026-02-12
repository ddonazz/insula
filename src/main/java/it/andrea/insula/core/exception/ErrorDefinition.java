package it.andrea.insula.core.exception;

public interface ErrorDefinition {
    int getCode();

    String getErrorCode();

    /**
     * @return A human-readable, non-i18n, default message (in English).
     */
    String getDefaultMessage();
}
