package it.andrea.insula.pricing.internal.rateplan.dto.request;

import it.andrea.insula.pricing.internal.core.AdjustmentType;
import it.andrea.insula.pricing.internal.rateplan.model.MealPlan;
import it.andrea.insula.pricing.internal.rateplan.model.RatePlanStatus;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record RatePlanPatchDto(
        String name,
        String description,
        MealPlan mealPlan,
        AdjustmentType adjustmentType,
        BigDecimal adjustmentValue,
        Integer minStay,
        Integer maxStay,
        Boolean closedToArrival,
        Boolean closedToDeparture,
        Boolean isDefault,
        Boolean requiresPromoCode,
        RatePlanStatus status
) {
}

