package it.andrea.insula.owner.internal.agreement.exception;

import it.andrea.insula.core.exception.ErrorDefinition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgreementErrorCodes implements ErrorDefinition {

    AGREEMENT_NOT_FOUND(50101, "agreement.not.found", "Agreement not found with the provided identifier."),
    AGREEMENT_DATES_INVALID(50102, "agreement.dates.invalid", "The start date must be before the end date."),
    AGREEMENT_UNIT_NOT_FOUND(50103, "agreement.unit.not.found", "The referenced unit was not found."),
    AGREEMENT_OVERLAP(50104, "agreement.overlap", "An active agreement already exists for this unit in the given period.");

    private final int code;
    private final String errorCode;
    private final String defaultMessage;
}

