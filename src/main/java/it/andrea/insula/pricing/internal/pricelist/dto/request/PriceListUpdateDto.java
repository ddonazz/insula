package it.andrea.insula.pricing.internal.pricelist.dto.request;

import it.andrea.insula.pricing.internal.pricelist.model.PriceListStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record PriceListUpdateDto(
        @NotBlank
        String name,

        String description,

        boolean isDefault,

        @NotNull
        @Size(min = 3, max = 3)
        String currency,

        @NotNull
        PriceListStatus status,

        UUID parentPriceListPublicId,

        BigDecimal percentageAdjustment,

        BigDecimal flatAdjustment
) {
}

