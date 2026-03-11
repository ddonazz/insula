package it.andrea.insula.user.internal.permission.dto.response;

import it.andrea.insula.core.dto.TranslatedEnum;

import java.util.List;

public record PermissionDomainGroupResponseDto(
        TranslatedEnum domain,
        List<PermissionResponseDto> permissions
) {
}

