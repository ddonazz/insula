package it.andrea.insula.pricing.internal.pricelist.dto.request;

import it.andrea.insula.pricing.internal.pricelist.model.PriceListStatus;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record PriceListPatchDto(
        String name,

        String description,

        Boolean isDefault,

        @Size(min = 3, max = 3)
        String currency,

        PriceListStatus status,

        UUID parentPriceListPublicId,

        BigDecimal percentageAdjustment,

        BigDecimal flatAdjustment
) {
}

