package it.andrea.insula.security;

import lombok.Getter;

@Getter
public enum PermissionAuthority {

    USER_READ("user:read", "Read user"),
    USER_CREATE("user:create", "Create user"),
    USER_UPDATE("user:update", "Update user"),
    USER_DELETE("user:delete", "Delete user"),

    ROLE_READ("role:read", "Read role"),
    ROLE_CREATE("role:create", "Create role"),
    ROLE_UPDATE("role:update", "Update role"),
    ROLE_DELETE("role:delete", "Delete role"),

    PERMISSION_READ("permission:read", "Read permission"),

    CUSTOMER_READ("customer:read", "Read customer"),
    CUSTOMER_CREATE("customer:create", "Create customer"),
    CUSTOMER_UPDATE("customer:update", "Update customer"),
    CUSTOMER_DELETE("customer:delete", "Delete customer"),

    PROPERTY_READ("property:read", "Read property"),
    PROPERTY_CREATE("property:create", "Create property"),
    PROPERTY_UPDATE("property:update", "Update property"),
    PROPERTY_DELETE("property:delete", "Delete property"),

    ADMIN_ACCESS("admin:access", "Admin access");

    private final String authority;
    private final String description;
    PermissionAuthority(String authority, String description) {
        this.authority = authority;
        this.description = description;
    }

    @Override
    public String toString() {
        return this.authority;
    }
}