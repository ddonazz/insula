package it.andrea.insula.pricing.internal.rate.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record RateCreateDto(
        @NotNull
        UUID unitPublicId,

        @NotNull
        LocalDate startDate,

        @NotNull
        LocalDate endDate,

        BigDecimal pricePerNight,

        BigDecimal extraGuestPrice,

        Integer minStay,

        Integer maxStay,

        boolean stopSell,

        boolean closedToArrival,

        boolean closedToDeparture,

        Set<DayOfWeek> allowedCheckInDays,

        Set<DayOfWeek> allowedCheckOutDays
) {
}

