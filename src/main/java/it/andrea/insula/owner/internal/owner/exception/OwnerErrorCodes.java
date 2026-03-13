package it.andrea.insula.owner.internal.owner.exception;

import it.andrea.insula.core.exception.ErrorDefinition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OwnerErrorCodes implements ErrorDefinition {

    OWNER_NOT_FOUND(50001, "owner.not.found", "Owner not found with the provided identifier."),
    OWNER_EMAIL_IN_USE(50002, "owner.email.inuse", "The email address is already in use by another owner."),
    OWNER_FISCAL_CODE_IN_USE(50003, "owner.fiscalcode.inuse", "The fiscal code is already in use by another owner."),
    OWNER_ALREADY_DELETED(50004, "owner.already.deleted", "The owner has already been deleted."),
    OWNER_VAT_NUMBER_IN_USE(50005, "owner.vatnumber.inuse", "The VAT number is already in use by another owner."),
    OWNER_TYPE_MISMATCH(50006, "owner.type.mismatch", "The owner type in the request does not match the existing owner type.");

    private final int code;
    private final String errorCode;
    private final String defaultMessage;
}

