package it.andrea.insula.pricing.internal.engine.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record NightBreakdownDto(
        LocalDate date,
        BigDecimal basePrice,
        BigDecimal planPrice
) {
}

