package it.andrea.insula.user.internal.role.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Set;

@Builder
public record RolePatchDto(
        @Size(min = 5, max = 255)
        String name,
        String description,
        Set<Long> permissions
) {
}

