package it.andrea.insula.pricing.internal.rate.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record CalendarDayDto(
        LocalDate date,
        BigDecimal pricePerNight,
        BigDecimal extraGuestPrice,
        Integer minStay,
        Integer maxStay,
        boolean stopSell,
        boolean closedToArrival,
        boolean closedToDeparture,
        String source,
        String seasonName
) {
}

