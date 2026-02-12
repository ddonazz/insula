package it.andrea.insula.user.internal.user.dto.response;

import it.andrea.insula.user.internal.role.dto.response.RoleResponseDto;
import lombok.Builder;

import java.util.Set;

@Builder
public record UserResponseDto(
        Long id,
        String username,
        String email,
        Set<RoleResponseDto> roles
) {
}
