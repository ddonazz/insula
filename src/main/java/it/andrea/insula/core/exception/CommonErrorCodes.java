package it.andrea.insula.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCodes implements ErrorDefinition {

    // --- Server Generic Errors (990xx) ---
    INTERNAL_SERVER_ERROR(99000, "server.internal.error", "An unexpected error occurred. Please contact support."),
    VALIDATION_FAILED(99001, "validation.failed", "Validation failed for one or more fields."),

    // --- Security Errors (991xx) ---
    UNAUTHENTICATED(99100, "security.unauthenticated", "Authentication is required to access this resource."),
    ACCESS_DENIED(99101, "security.access.denied", "Access Denied. You do not have the necessary permissions.");

    private final int code;
    private final String errorCode;
    private final String defaultMessage;
}
