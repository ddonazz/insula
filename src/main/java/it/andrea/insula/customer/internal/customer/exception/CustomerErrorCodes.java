package it.andrea.insula.customer.internal.customer.exception;

import it.andrea.insula.core.exception.ErrorDefinition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomerErrorCodes implements ErrorDefinition {

    CUSTOMER_NOT_FOUND(20001, "customer.not.found", "Customer not found with the provided identifier."),
    CUSTOMER_EMAIL_IN_USE(20002, "customer.email.inuse", "The email address is already in use by another customer."),
    CUSTOMER_FISCAL_CODE_IN_USE(20003, "customer.fiscalcode.inuse", "The fiscal code is already in use by another customer."),
    INDIVIDUAL_NOT_FOUND(20004, "customer.individual.not.found", "Individual customer not found with the provided identifier."),
    BUSINESS_NOT_FOUND(20005, "customer.business.not.found", "Business customer not found with the provided identifier."),
    CONTACT_NOT_FOUND(20006, "customer.contact.not.found", "Contact not found with the provided identifier."),
    ADDRESS_NOT_FOUND(20007, "customer.address.not.found", "Address not found with the provided identifier."),
    VAT_NUMBER_IN_USE(20008, "customer.vatnumber.inuse", "The VAT number is already in use by another customer."),
    CUSTOMER_TYPE_MISMATCH(20009, "customer.type.mismatch", "The customer type in the request does not match the existing customer type.");

    private final int code;
    private final String errorCode;
    private final String defaultMessage;
}

