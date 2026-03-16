package it.andrea.insula.pricing.internal.rateplan.dto.request;

import it.andrea.insula.pricing.internal.core.AdjustmentType;
import it.andrea.insula.pricing.internal.rateplan.model.MealPlan;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record RatePlanCreateDto(
        @NotBlank
        String name,
        String description,
        @NotNull
        MealPlan mealPlan,
        AdjustmentType adjustmentType,
        BigDecimal adjustmentValue,
        Integer minStay,
        Integer maxStay,
        boolean closedToArrival,
        boolean closedToDeparture,
        boolean isDefault,
        boolean requiresPromoCode
) {
}

