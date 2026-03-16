package it.andrea.insula.pricing.internal.rateplan.dto.response;

import it.andrea.insula.core.dto.TranslatedEnum;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record RatePlanResponseDto(
        UUID publicId,
        UUID priceListPublicId,
        String priceListName,
        String name,
        String description,
        TranslatedEnum mealPlan,
        TranslatedEnum adjustmentType,
        BigDecimal adjustmentValue,
        Integer minStay,
        Integer maxStay,
        boolean closedToArrival,
        boolean closedToDeparture,
        boolean isDefault,
        boolean requiresPromoCode,
        TranslatedEnum status
) {
}

