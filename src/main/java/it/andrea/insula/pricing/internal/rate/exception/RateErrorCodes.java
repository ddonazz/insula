package it.andrea.insula.pricing.internal.rate.exception;

import it.andrea.insula.core.exception.ErrorDefinition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RateErrorCodes implements ErrorDefinition {

    RATE_NOT_FOUND(60101, "rate.not.found", "Rate period not found with the provided identifier."),
    RATE_DATES_INVALID(60102, "rate.dates.invalid", "The start date must be before the end date."),
    RATE_UNIT_NOT_FOUND(60103, "rate.unit.not.found", "The referenced unit was not found."),
    RATE_OVERLAP(60104, "rate.overlap", "A rate period already exists for this unit in the given date range within this price list."),
    RATE_MIN_MAX_STAY_INVALID(60105, "rate.minstay.invalid", "The minimum stay must be less than or equal to the maximum stay.");

    private final int code;
    private final String errorCode;
    private final String defaultMessage;
}

