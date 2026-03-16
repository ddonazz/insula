package it.andrea.insula.pricing.internal.rule.dto.request;

import it.andrea.insula.pricing.internal.rule.model.PricingRuleStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record PricingRuleSearchCriteria(
        UUID priceListPublicId,
        UUID ratePlanPublicId,
        String name,
        PricingRuleStatus status,
        PricingRuleType type
) {
}

