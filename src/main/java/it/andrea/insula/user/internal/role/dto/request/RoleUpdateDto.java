package it.andrea.insula.user.internal.role.dto.request;

import lombok.Builder;

import java.util.Set;

@Builder
public record RoleUpdateDto(
        String name,
        String description,
        Set<Long> permissions
) {
}
