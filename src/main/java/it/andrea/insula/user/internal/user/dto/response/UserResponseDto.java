package it.andrea.insula.user.internal.user.dto.response;

import it.andrea.insula.core.dto.TranslatedEnum;
import it.andrea.insula.user.internal.role.dto.response.RoleResponseDto;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record UserResponseDto(
        UUID publicId,
        String username,
        String email,
        TranslatedEnum status,
        Set<RoleResponseDto> roles
) {
}
