package it.andrea.insula.pricing.internal.pricelist.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record PriceListCreateDto(
        @NotBlank
        String name,

        String description,

        boolean isDefault,

        @Size(min = 3, max = 3)
        String currency,

        UUID parentPriceListPublicId,

        BigDecimal percentageAdjustment,

        BigDecimal flatAdjustment
) {
}

