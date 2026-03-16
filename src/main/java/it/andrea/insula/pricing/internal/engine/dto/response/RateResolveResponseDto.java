package it.andrea.insula.pricing.internal.engine.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record RateResolveResponseDto(
        boolean available,
        RestrictionCheckDto restrictions,
        String currency,
        int nights,
        List<NightBreakdownDto> nightsBreakdown,
        BigDecimal subtotalBeforeRules,
        List<AppliedRuleDto> rulesApplied,
        BigDecimal subtotalAfterRules,
        List<AppliedPromotionDto> promotionsApplied,
        BigDecimal total
) {
}

