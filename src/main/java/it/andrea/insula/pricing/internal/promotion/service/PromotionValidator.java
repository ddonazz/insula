package it.andrea.insula.pricing.internal.promotion.service;

import it.andrea.insula.core.exception.BusinessRuleException;
import it.andrea.insula.pricing.internal.promotion.exception.PromotionErrorCodes;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class PromotionValidator {

    public void validateDateRanges(LocalDate bookingFrom, LocalDate bookingTo, LocalDate stayFrom, LocalDate stayTo) {
        if (bookingTo != null && bookingFrom != null && bookingFrom.isAfter(bookingTo)) {
            throw new BusinessRuleException(PromotionErrorCodes.PROMOTION_DATE_RANGE_INVALID);
        }
        if (stayFrom != null && stayTo != null && stayFrom.isAfter(stayTo)) {
            throw new BusinessRuleException(PromotionErrorCodes.PROMOTION_DATE_RANGE_INVALID);
        }
    }

    public void validateValues(Integer minNights, Integer maxUsages, BigDecimal discountValue) {
        if (minNights != null && minNights < 1) {
            throw new BusinessRuleException(PromotionErrorCodes.PROMOTION_MIN_NIGHTS_INVALID);
        }
        if (maxUsages != null && maxUsages < 1) {
            throw new BusinessRuleException(PromotionErrorCodes.PROMOTION_MAX_USAGES_INVALID);
        }
        if (discountValue != null && discountValue.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessRuleException(PromotionErrorCodes.PROMOTION_DISCOUNT_INVALID);
        }
    }
}

