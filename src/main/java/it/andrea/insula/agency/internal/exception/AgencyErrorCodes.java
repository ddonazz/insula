package it.andrea.insula.agency.internal.exception;

import it.andrea.insula.core.exception.ErrorDefinition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AgencyErrorCodes implements ErrorDefinition {

    AGENCY_NOT_FOUND(30001, "agency.not.found", "Agency not found with the provided identifier."),
    VAT_NUMBER_ALREADY_EXISTS(30002, "agency.vatnumber.inuse", "The VAT number is already in use by another agency."),
    PEC_EMAIL_ALREADY_EXISTS(30003, "agency.pecemail.inuse", "The PEC email is already in use by another agency.");

    private final int code;
    private final String errorCode;
    private final String defaultMessage;
}

