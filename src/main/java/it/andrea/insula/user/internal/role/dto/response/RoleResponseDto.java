package it.andrea.insula.user.internal.role.dto.response;

import it.andrea.insula.user.internal.permission.dto.response.PermissionResponseDto;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record RoleResponseDto(
        UUID publicId,
        String name,
        String description,
        Set<PermissionResponseDto> permissions
) {
}
