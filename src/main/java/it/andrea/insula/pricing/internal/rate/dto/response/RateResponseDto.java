package it.andrea.insula.pricing.internal.rate.dto.response;

import it.andrea.insula.core.dto.TranslatedEnum;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Builder
public record RateResponseDto(
        UUID publicId,
        UUID priceListPublicId,
        String priceListName,
        UUID unitPublicId,
        UnitSummaryDto unit,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal pricePerNight,
        BigDecimal extraGuestPrice,
        Integer minStay,
        Integer maxStay,
        boolean stopSell,
        boolean closedToArrival,
        boolean closedToDeparture,
        Set<TranslatedEnum> allowedCheckInDays,
        Set<TranslatedEnum> allowedCheckOutDays
) {

    @Builder
    public record UnitSummaryDto(
            UUID publicId,
            UUID propertyPublicId,
            String propertyName,
            String internalName,
            String type,
            String floor,
            String internalNumber
    ) {
    }
}

