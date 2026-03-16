package it.andrea.insula.pricing.internal.rate.dto.response;

import it.andrea.insula.core.dto.TranslatedEnum;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record RateResponseDto(
        UUID publicId,
        UUID priceListPublicId,
        String priceListName,
        UUID sourceSeasonPublicId,
        String sourceSeasonName,
        UUID unitPublicId,
        UnitSummaryDto unit,
        LocalDate stayDate,
        BigDecimal pricePerNight,
        BigDecimal extraGuestPrice,
        Integer minStay,
        Integer maxStay,
        boolean stopSell,
        boolean closedToArrival,
        boolean closedToDeparture,
        TranslatedEnum sourceSeasonType
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

