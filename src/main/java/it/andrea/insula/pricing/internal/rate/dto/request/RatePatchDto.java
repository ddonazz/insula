package it.andrea.insula.pricing.internal.rate.dto.request;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record RatePatchDto(
        UUID unitPublicId,

        LocalDate stayDate,

        UUID sourceSeasonPublicId,

        BigDecimal pricePerNight,

        BigDecimal extraGuestPrice,

        Integer minStay,

        Integer maxStay,

        Boolean stopSell,

        Boolean closedToArrival,

        Boolean closedToDeparture
) {
}

