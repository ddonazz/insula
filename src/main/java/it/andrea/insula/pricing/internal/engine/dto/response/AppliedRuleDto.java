package it.andrea.insula.pricing.internal.engine.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AppliedRuleDto(
        String ruleName,
        String ruleType,
        String adjustmentType,
        BigDecimal adjustmentValue,
        BigDecimal impact
) {
}

