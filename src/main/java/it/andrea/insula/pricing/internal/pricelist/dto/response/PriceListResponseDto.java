package it.andrea.insula.pricing.internal.pricelist.dto.response;

import it.andrea.insula.core.dto.TranslatedEnum;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record PriceListResponseDto(
        UUID publicId,
        String name,
        String description,
        boolean isDefault,
        String currency,
        TranslatedEnum status,
        UUID parentPriceListPublicId,
        String parentPriceListName,
        BigDecimal percentageAdjustment,
        BigDecimal flatAdjustment
) {
}

