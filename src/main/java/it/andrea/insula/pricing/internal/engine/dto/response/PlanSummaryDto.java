package it.andrea.insula.pricing.internal.engine.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record PlanSummaryDto(
        UUID ratePlanPublicId,
        String ratePlanName,
        String mealPlan,
        BigDecimal total
) {
}

