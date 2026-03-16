package it.andrea.insula.pricing.internal.rate.exception;

import it.andrea.insula.core.exception.ErrorDefinition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RateErrorCodes implements ErrorDefinition {

    RATE_NOT_FOUND(4101, "rate.not.found", "Rate day not found."),
    RATE_UNIT_NOT_FOUND(4102, "rate.unit.not.found", "Unit not found."),
    RATE_SOURCE_SEASON_NOT_FOUND(4103, "rate.source.season.not.found", "Source season not found."),
    RATE_MIN_MAX_STAY_INVALID(4104, "rate.minstay.invalid", "Minimum stay must be less than or equal to maximum stay."),
    RATE_DUPLICATE_DAY(4105, "rate.duplicate.day", "A rate already exists for this unit and stay date.");

    private final int code;
    private final String errorCode;
    private final String defaultMessage;
}

