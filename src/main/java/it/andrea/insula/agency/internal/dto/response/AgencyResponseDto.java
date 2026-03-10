package it.andrea.insula.agency.internal.dto.response;

import it.andrea.insula.core.dto.TranslatedEnum;
import lombok.Builder;

import java.util.UUID;

@Builder
public record AgencyResponseDto(
        Long id,
        UUID publicId,
        String name,
        String vatNumber,
        String fiscalCode,
        String sdiCode,
        String pecEmail,
        String contactEmail,
        String phoneNumber,
        String websiteUrl,
        String logoUrl,
        String timeZone,
        TranslatedEnum status
) {
}

