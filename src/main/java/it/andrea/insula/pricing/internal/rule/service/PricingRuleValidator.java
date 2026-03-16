package it.andrea.insula.pricing.internal.rule.service;

import it.andrea.insula.core.exception.BusinessRuleException;
import it.andrea.insula.pricing.internal.rule.dto.request.PricingRuleType;
import it.andrea.insula.pricing.internal.rule.exception.PricingRuleErrorCodes;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class PricingRuleValidator {

    public void validateCommon(BigDecimal adjustmentValue, int priority) {
        if (adjustmentValue == null) {
            throw new BusinessRuleException(PricingRuleErrorCodes.PRICING_RULE_VALUE_INVALID);
        }
        if (priority < 0) {
            throw new BusinessRuleException(PricingRuleErrorCodes.PRICING_RULE_VALUE_INVALID);
        }
    }

    public void validateByType(
            PricingRuleType type,
            Integer minNights,
            Integer maxNights,
            Integer minDaysInAdvance,
            Integer maxDaysInAdvance,
            java.util.Set<java.time.DayOfWeek> applyOnDays,
            Integer guestsThreshold,
            Integer minStayRequired
    ) {
        if (type == null) {
            throw new BusinessRuleException(PricingRuleErrorCodes.PRICING_RULE_TYPE_INVALID);
        }
        switch (type) {
            case LOS -> {
                if (minNights == null && maxNights == null) {
                    throw new BusinessRuleException(PricingRuleErrorCodes.PRICING_RULE_TYPE_INVALID);
                }
            }
            case LEAD_TIME -> {
                if (minDaysInAdvance == null && maxDaysInAdvance == null) {
                    throw new BusinessRuleException(PricingRuleErrorCodes.PRICING_RULE_TYPE_INVALID);
                }
            }
            case DAY_OF_WEEK -> {
                if (applyOnDays == null || applyOnDays.isEmpty()) {
                    throw new BusinessRuleException(PricingRuleErrorCodes.PRICING_RULE_TYPE_INVALID);
                }
            }
            case OCCUPANCY -> {
                if (guestsThreshold == null || guestsThreshold < 1) {
                    throw new BusinessRuleException(PricingRuleErrorCodes.PRICING_RULE_TYPE_INVALID);
                }
            }
            case MIN_STAY -> {
                if (minStayRequired == null || minStayRequired < 1) {
                    throw new BusinessRuleException(PricingRuleErrorCodes.PRICING_RULE_TYPE_INVALID);
                }
            }
        }
    }
}

