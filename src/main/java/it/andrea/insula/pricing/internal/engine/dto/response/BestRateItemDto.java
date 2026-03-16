package it.andrea.insula.pricing.internal.engine.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record BestRateItemDto(
        UUID unitPublicId,
        boolean available,
        String unavailableReason,
        PlanSummaryDto bestPlan,
        PlanSummaryDto defaultPlan
) {
}

