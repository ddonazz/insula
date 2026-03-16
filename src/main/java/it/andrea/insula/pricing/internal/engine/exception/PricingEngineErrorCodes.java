package it.andrea.insula.pricing.internal.engine.exception;

import it.andrea.insula.core.exception.ErrorDefinition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PricingEngineErrorCodes implements ErrorDefinition {

    RESOLVE_DATES_INVALID(62001, "pricing.resolve.dates.invalid", "Check-in must be before check-out."),
    RESOLVE_DATES_IN_PAST(62002, "pricing.resolve.dates.in.past", "Check-out date must be in the future."),
    RESOLVE_UNIT_NOT_FOUND(62003, "pricing.resolve.unit.not.found", "No rate data found for the requested stay."),
    RESOLVE_PLAN_NOT_FOUND(62004, "pricing.resolve.plan.not.found", "Rate plan not found for the price list."),
    AVAILABILITY_RANGE_TOO_WIDE(62005, "pricing.availability.range.wide", "Availability range cannot exceed 366 days.");

    private final int code;
    private final String errorCode;
    private final String defaultMessage;
}

