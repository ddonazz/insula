package it.andrea.insula.pricing.internal.rate.dto.request;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record RatePatchDto(
        UUID unitPublicId,

        LocalDate startDate,

        LocalDate endDate,

        BigDecimal pricePerNight,

        BigDecimal extraGuestPrice,

        Integer minStay,

        Integer maxStay,

        Boolean stopSell,

        Boolean closedToArrival,

        Boolean closedToDeparture,

        Set<DayOfWeek> allowedCheckInDays,

        Set<DayOfWeek> allowedCheckOutDays
) {
}

