package it.andrea.insula.pricing.internal.rule.exception;

import it.andrea.insula.core.exception.ErrorDefinition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PricingRuleErrorCodes implements ErrorDefinition {

    PRICING_RULE_NOT_FOUND(4501, "pricingrule.not.found", "Pricing rule not found."),
    PRICING_RULE_PRICE_LIST_NOT_FOUND(4502, "pricingrule.pricelist.not.found", "Price list not found."),
    PRICING_RULE_RATE_PLAN_NOT_FOUND(4503, "pricingrule.rateplan.not.found", "Rate plan not found."),
    PRICING_RULE_TYPE_INVALID(4504, "pricingrule.type.invalid", "Rule type is invalid or missing required fields."),
    PRICING_RULE_VALUE_INVALID(4505, "pricingrule.value.invalid", "Rule values are invalid.");

    private final int code;
    private final String errorCode;
    private final String defaultMessage;
}

