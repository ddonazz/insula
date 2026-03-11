package it.andrea.insula.user.internal.user.exception;

import it.andrea.insula.core.exception.ErrorDefinition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorCodes implements ErrorDefinition {

    USER_NOT_FOUND(10001, "user.not.found", "User not found with the provided identifier."),
    EMAIL_ALREADY_EXISTS(10002, "user.email.exists", "The email address is already in use."),
    USERNAME_ALREADY_EXISTS(10003, "user.username.exists", "The username is already taken."),
    ADMIN_USER_IMMUTABLE(10004, "user.admin.immutable", "The system administrator user cannot be modified or deleted."),
    ADMIN_ROLE_IMMUTABLE(10005, "role.admin.immutable", "The system administrator role cannot be modified or deleted."),
    INVALID_TENANT_FOR_USER(10007, "user.invalid.tenant", "Non-admin users cannot be assigned to the default tenant."),

    ROLE_NOT_FOUND(10101, "role.not.found", "Role not found with the provided identifier."),
    ROLE_NAME_EXISTS(10102, "role.name.exists", "A role with this name already exists."),
    ROLE_IN_USE(10103, "role.in.use", "Cannot delete role because it is assigned to one or more users."),

    PERMISSION_NOT_FOUND(10201, "permission.not.found", "Permission not found with the provided identifier.");

    private final int code;
    private final String errorCode;
    private final String defaultMessage;

}
