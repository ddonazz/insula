package it.andrea.insula.user.internal.role.dto.response;

import it.andrea.insula.user.internal.permission.dto.response.PermissionResponseDto;
import lombok.Builder;

import java.util.Set;

@Builder
public record RoleResponseDto(
        String name,
        String description,
        Set<PermissionResponseDto> permissions
) {
}
