package it.andrea.insula.pricing.internal.rateplan.dto.request;

import it.andrea.insula.pricing.internal.rateplan.model.MealPlan;
import it.andrea.insula.pricing.internal.rateplan.model.RatePlanStatus;
import lombok.Builder;

@Builder
public record RatePlanSearchCriteria(
        String name,
        MealPlan mealPlan,
        RatePlanStatus status,
        Boolean isDefault,
        Boolean requiresPromoCode
) {
}

