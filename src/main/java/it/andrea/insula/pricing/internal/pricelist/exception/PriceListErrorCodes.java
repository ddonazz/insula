package it.andrea.insula.pricing.internal.pricelist.exception;

import it.andrea.insula.core.exception.ErrorDefinition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PriceListErrorCodes implements ErrorDefinition {

    PRICELIST_NOT_FOUND(4001, "pricelist.not.found", "Price list not found."),
    PRICELIST_NAME_IN_USE(4002, "pricelist.name.inuse", "Price list name already in use."),
    PRICELIST_ALREADY_DELETED(4003, "pricelist.already.deleted", "Price list already deleted."),
    PRICELIST_DEFAULT_ALREADY_EXISTS(4004, "pricelist.default.already.exists", "Default price list already exists."),
    PRICELIST_PARENT_NOT_FOUND(4005, "pricelist.parent.not.found", "Parent price list not found."),
    PRICELIST_HAS_DERIVED(4006, "pricelist.has.derived", "Price list has derived price lists.");

    private final int code;
    private final String errorCode;
    private final String defaultMessage;
}

