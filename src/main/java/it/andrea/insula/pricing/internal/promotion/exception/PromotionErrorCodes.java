package it.andrea.insula.pricing.internal.promotion.exception;

import it.andrea.insula.core.exception.ErrorDefinition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PromotionErrorCodes implements ErrorDefinition {

    PROMOTION_NOT_FOUND(4401, "promotion.not.found", "Promotion not found."),
    PROMOTION_PRICE_LIST_NOT_FOUND(4402, "promotion.pricelist.not.found", "Price list not found."),
    PROMOTION_DATE_RANGE_INVALID(4403, "promotion.dates.invalid", "Invalid booking or stay date range."),
    PROMOTION_MIN_NIGHTS_INVALID(4404, "promotion.min.nights.invalid", "Minimum nights must be positive."),
    PROMOTION_MAX_USAGES_INVALID(4405, "promotion.max.usages.invalid", "Maximum usages must be positive."),
    PROMOTION_DISCOUNT_INVALID(4406, "promotion.discount.invalid", "Discount value must be positive.");

    private final int code;
    private final String errorCode;
    private final String defaultMessage;
}

