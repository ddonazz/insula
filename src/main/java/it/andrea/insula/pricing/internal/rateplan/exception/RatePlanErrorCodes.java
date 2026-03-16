package it.andrea.insula.pricing.internal.rateplan.exception;

import it.andrea.insula.core.exception.ErrorDefinition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RatePlanErrorCodes implements ErrorDefinition {

    RATE_PLAN_NOT_FOUND(4201, "rateplan.not.found", "Rate plan not found."),
    RATE_PLAN_NAME_IN_USE(4202, "rateplan.name.inuse", "Rate plan name already in use."),
    RATE_PLAN_PRICE_LIST_NOT_FOUND(4203, "rateplan.pricelist.not.found", "Price list not found."),
    RATE_PLAN_DEFAULT_ALREADY_EXISTS(4204, "rateplan.default.already.exists", "A default rate plan already exists."),
    RATE_PLAN_MIN_MAX_STAY_INVALID(4205, "rateplan.minstay.invalid", "Minimum stay must be less than or equal to maximum stay.");

    private final int code;
    private final String errorCode;
    private final String defaultMessage;
}

