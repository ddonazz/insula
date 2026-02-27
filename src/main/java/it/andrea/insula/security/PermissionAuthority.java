package it.andrea.insula.security;

import lombok.Getter;

@Getter
public enum PermissionAuthority {

    USER_READ("user:read", "Read user", "USER"),
    USER_CREATE("user:create", "Create user", "USER"),
    USER_UPDATE("user:update", "Update user", "USER"),
    USER_DELETE("user:delete", "Delete user", "USER"),

    ROLE_READ("role:read", "Read role", "ROLE"),
    ROLE_CREATE("role:create", "Create role", "ROLE"),
    ROLE_UPDATE("role:update", "Update role", "ROLE"),
    ROLE_DELETE("role:delete", "Delete role", "ROLE"),

    PERMISSION_READ("permission:read", "Read permission", "PERMISSION"),

    CUSTOMER_READ("customer:read", "Read customer", "CUSTOMER"),
    CUSTOMER_CREATE("customer:create", "Create customer", "CUSTOMER"),
    CUSTOMER_UPDATE("customer:update", "Update customer", "CUSTOMER"),
    CUSTOMER_DELETE("customer:delete", "Delete customer", "CUSTOMER"),

    PROPERTY_READ("property:read", "Read property", "PROPERTY"),
    PROPERTY_CREATE("property:create", "Create property", "PROPERTY"),
    PROPERTY_UPDATE("property:update", "Update property", "PROPERTY"),
    PROPERTY_DELETE("property:delete", "Delete property", "PROPERTY"),

    ADMIN_ACCESS("admin:access", "Admin access", "ADMIN");

    private final String authority;
    private final String description;
    private final String domain;

    PermissionAuthority(String authority, String description, String domain) {
        this.authority = authority;
        this.description = description;
        this.domain = domain;
    }

    @Override
    public String toString() {
        return this.authority;
    }
}
