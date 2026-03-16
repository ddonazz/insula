package it.andrea.insula.pricing.internal.season.dto.response;

import it.andrea.insula.core.dto.TranslatedEnum;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record SeasonResponseDto(
        UUID publicId,
        UUID priceListPublicId,
        String priceListName,
        String name,
        TranslatedEnum seasonType,
        LocalDate startDate,
        LocalDate endDate,
        int priority,
        TranslatedEnum status
) {
}

