package it.andrea.insula.security;

import lombok.Getter;

@Getter
public enum PermissionAuthority {

    USER_READ(Constants.USER_READ, "Read user", Domains.USER),
    USER_CREATE(Constants.USER_CREATE, "Create user", Domains.USER),
    USER_UPDATE(Constants.USER_UPDATE, "Update user", Domains.USER),
    USER_DELETE(Constants.USER_DELETE, "Delete user", Domains.USER),

    ROLE_READ(Constants.ROLE_READ, "Read role", Domains.ROLE),
    ROLE_CREATE(Constants.ROLE_CREATE, "Create role", Domains.ROLE),
    ROLE_UPDATE(Constants.ROLE_UPDATE, "Update role", Domains.ROLE),
    ROLE_DELETE(Constants.ROLE_DELETE, "Delete role", Domains.ROLE),

    PERMISSION_READ(Constants.PERMISSION_READ, "Read permission", Domains.PERMISSION),

    CUSTOMER_READ(Constants.CUSTOMER_READ, "Read customer", Domains.CUSTOMER),
    CUSTOMER_CREATE(Constants.CUSTOMER_CREATE, "Create customer", Domains.CUSTOMER),
    CUSTOMER_UPDATE(Constants.CUSTOMER_UPDATE, "Update customer", Domains.CUSTOMER),
    CUSTOMER_DELETE(Constants.CUSTOMER_DELETE, "Delete customer", Domains.CUSTOMER),

    AGENCY_READ(Constants.AGENCY_READ, "Read agency", Domains.AGENCY),
    AGENCY_CREATE(Constants.AGENCY_CREATE, "Create agency", Domains.AGENCY),
    AGENCY_UPDATE(Constants.AGENCY_UPDATE, "Update agency", Domains.AGENCY),
    AGENCY_DELETE(Constants.AGENCY_DELETE, "Delete agency", Domains.AGENCY),

    PROPERTY_READ(Constants.PROPERTY_READ, "Read property", Domains.PROPERTY),
    PROPERTY_CREATE(Constants.PROPERTY_CREATE, "Create property", Domains.PROPERTY),
    PROPERTY_UPDATE(Constants.PROPERTY_UPDATE, "Update property", Domains.PROPERTY),
    PROPERTY_DELETE(Constants.PROPERTY_DELETE, "Delete property", Domains.PROPERTY),

    UNIT_READ(Constants.UNIT_READ, "Read unit", Domains.UNIT),
    UNIT_CREATE(Constants.UNIT_CREATE, "Create unit", Domains.UNIT),
    UNIT_UPDATE(Constants.UNIT_UPDATE, "Update unit", Domains.UNIT),
    UNIT_DELETE(Constants.UNIT_DELETE, "Delete unit", Domains.UNIT),

    ADMIN_ACCESS(Constants.ADMIN_ACCESS, "Admin access", Domains.ADMIN);

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

    public static class Constants {
        public static final String USER_READ = "user:read";
        public static final String USER_CREATE = "user:create";
        public static final String USER_UPDATE = "user:update";
        public static final String USER_DELETE = "user:delete";

        public static final String ROLE_READ = "role:read";
        public static final String ROLE_CREATE = "role:create";
        public static final String ROLE_UPDATE = "role:update";
        public static final String ROLE_DELETE = "role:delete";

        public static final String PERMISSION_READ = "permission:read";

        public static final String CUSTOMER_READ = "customer:read";
        public static final String CUSTOMER_CREATE = "customer:create";
        public static final String CUSTOMER_UPDATE = "customer:update";
        public static final String CUSTOMER_DELETE = "customer:delete";

        public static final String AGENCY_READ = "agency:read";
        public static final String AGENCY_CREATE = "agency:create";
        public static final String AGENCY_UPDATE = "agency:update";
        public static final String AGENCY_DELETE = "agency:delete";

        public static final String PROPERTY_READ = "property:read";
        public static final String PROPERTY_CREATE = "property:create";
        public static final String PROPERTY_UPDATE = "property:update";
        public static final String PROPERTY_DELETE = "property:delete";

        public static final String UNIT_READ = "unit:read";
        public static final String UNIT_CREATE = "unit:create";
        public static final String UNIT_UPDATE = "unit:update";
        public static final String UNIT_DELETE = "unit:delete";

        public static final String ADMIN_ACCESS = "admin:access";
    }

    public static class Domains {
        public static final String USER = "USER";
        public static final String ROLE = "ROLE";
        public static final String PERMISSION = "PERMISSION";
        public static final String CUSTOMER = "CUSTOMER";
        public static final String AGENCY = "AGENCY";
        public static final String PROPERTY = "PROPERTY";
        public static final String UNIT = "UNIT";
        public static final String ADMIN = "ADMIN";
    }
}