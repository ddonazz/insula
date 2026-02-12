package it.andrea.insula.user.internal.permission.dto.response;

public record PermissionResponseDto(
        Long id,
        String authority,
        String description
) {
}