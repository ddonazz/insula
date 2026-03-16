package it.andrea.insula.pricing.internal.rule.dto.request;

import it.andrea.insula.pricing.internal.core.AdjustmentType;
import it.andrea.insula.pricing.internal.rule.model.PricingRuleStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Builder
public record PricingRulePatchDto(
        UUID priceListPublicId,
        UUID ratePlanPublicId,
        PricingRuleType type,
        String name,
        AdjustmentType adjustmentType,
        BigDecimal adjustmentValue,
        Integer priority,
        Boolean stackable,
        PricingRuleStatus status,
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

