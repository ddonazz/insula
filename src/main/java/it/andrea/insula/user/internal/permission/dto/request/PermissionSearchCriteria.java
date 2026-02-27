package it.andrea.insula.user.internal.permission.dto.request;

public record PermissionSearchCriteria(
        String authority,
        String description,
        String domain
) {
}
