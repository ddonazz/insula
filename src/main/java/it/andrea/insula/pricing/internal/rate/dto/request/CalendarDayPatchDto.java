package it.andrea.insula.pricing.internal.rate.dto.request;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CalendarDayPatchDto(
        BigDecimal pricePerNight,
        BigDecimal extraGuestPrice,
        Integer minStay,
        Integer maxStay,
        Boolean stopSell,
        Boolean closedToArrival,
        Boolean closedToDeparture
) {
}

