package it.andrea.insula.pricing.internal.engine.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record AvailabilityDayDto(
        LocalDate date,
        boolean available,
        boolean closedToArrival,
        boolean closedToDeparture,
        Integer minStay,
        BigDecimal basePrice,
        BigDecimal price,
        BigDecimal ratePlanAdjustment
) {
}

