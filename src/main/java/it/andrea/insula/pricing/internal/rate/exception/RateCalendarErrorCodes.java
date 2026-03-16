package it.andrea.insula.pricing.internal.rate.exception;

import it.andrea.insula.core.exception.ErrorDefinition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RateCalendarErrorCodes implements ErrorDefinition {

    CALENDAR_DATE_RANGE_INVALID(61101, "calendar.date.range.invalid", "Start date must be before end date."),
    CALENDAR_RANGE_TOO_WIDE(61102, "calendar.range.too.wide", "Date range cannot exceed 366 days."),
    CALENDAR_UNIT_REQUIRED(61103, "calendar.unit.required", "Unit public ID is required.");

    private final int code;
    private final String errorCode;
    private final String defaultMessage;
}

