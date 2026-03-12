package it.andrea.insula.pricing.internal.pricelist.exception;

import it.andrea.insula.core.exception.ErrorDefinition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PriceListErrorCodes implements ErrorDefinition {

    PRICELIST_NOT_FOUND(60001, "pricelist.not.found", "Price list not found with the provided identifier."),
    PRICELIST_NAME_IN_USE(60002, "pricelist.name.inuse", "The price list name is already in use."),
    PRICELIST_ALREADY_DELETED(60003, "pricelist.already.deleted", "The price list has already been deleted."),
    PRICELIST_DEFAULT_ALREADY_EXISTS(60004, "pricelist.default.already.exists", "A default price list already exists."),
    PRICELIST_PARENT_NOT_FOUND(60005, "pricelist.parent.not.found", "The parent price list was not found."),
    PRICELIST_HAS_DERIVED(60006, "pricelist.has.derived", "The price list cannot be deleted because it has derived price lists.");

    private final int code;
    private final String errorCode;
    private final String defaultMessage;
}

