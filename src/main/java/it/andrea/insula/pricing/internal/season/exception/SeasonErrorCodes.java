package it.andrea.insula.pricing.internal.season.exception;

import it.andrea.insula.core.exception.ErrorDefinition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SeasonErrorCodes implements ErrorDefinition {

    SEASON_NOT_FOUND(4301, "season.not.found", "Season not found."),
    SEASON_PRICE_LIST_NOT_FOUND(4302, "season.pricelist.not.found", "Price list not found."),
    SEASON_DATE_RANGE_INVALID(4303, "season.dates.invalid", "Start date must be before or equal to end date."),
    SEASON_PRIORITY_INVALID(4304, "season.priority.invalid", "Priority must be zero or positive."),

    SEASON_ALREADY_DELETED(61002, "season.already.deleted", "Season already deleted."),
    SEASON_UNIT_LIST_EMPTY(61003, "season.unit.list.empty", "At least one unit is required."),
    SEASON_PRICE_LIST_MISMATCH(61004, "season.pricelist.mismatch", "Season does not match the price list.");

    private final int code;
    private final String errorCode;
    private final String defaultMessage;
}

