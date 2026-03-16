package it.andrea.insula.pricing.internal.rule.dto.response;

import it.andrea.insula.core.dto.TranslatedEnum;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Builder
public record PricingRuleResponseDto(
        UUID publicId,
        UUID priceListPublicId,
        UUID ratePlanPublicId,
        String name,
        String type,
        TranslatedEnum adjustmentType,
        BigDecimal adjustmentValue,
        int priority,
        boolean stackable,
        TranslatedEnum status,
        Integer minNights,
        Integer maxNights,
        Integer minDaysInAdvance,
        Integer maxDaysInAdvance,
        Set<DayOfWeek> applyOnDays,
        Integer guestsThreshold,
        LocalDate applyFromDate,
        LocalDate applyToDate,
        Integer minStayRequired
) {
}

