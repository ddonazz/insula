package it.andrea.insula.pricing.internal.rate.dto.request;

import lombok.Builder;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record RateUpdateDto(
        @NotNull
        UUID unitPublicId,

        @NotNull
        LocalDate stayDate,

        UUID sourceSeasonPublicId,

        BigDecimal pricePerNight,

        BigDecimal extraGuestPrice,

        Integer minStay,

        Integer maxStay,

        boolean stopSell,

        boolean closedToArrival,

        boolean closedToDeparture
) {
}

