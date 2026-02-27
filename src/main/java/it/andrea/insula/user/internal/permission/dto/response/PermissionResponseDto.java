package it.andrea.insula.user.internal.permission.dto.response;

import it.andrea.insula.core.dto.TranslatedEnum;

public record PermissionResponseDto(
        Long id,
        String authority,
        String description,
        TranslatedEnum domain
) {
}
