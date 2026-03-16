package it.andrea.insula.pricing.internal.rule.dto.request;

import it.andrea.insula.pricing.internal.core.AdjustmentType;
import it.andrea.insula.pricing.internal.rule.model.PricingRuleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Builder
public record PricingRuleUpdateDto(
        UUID priceListPublicId,
        UUID ratePlanPublicId,
        @NotNull
        PricingRuleType type,
        @NotBlank
        String name,
        @NotNull
        AdjustmentType adjustmentType,
        @NotNull
        BigDecimal adjustmentValue,
        int priority,
        boolean stackable,
        @NotNull
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

