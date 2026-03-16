package it.andrea.insula.pricing.internal.engine.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AppliedPromotionDto(
        String promotionName,
        String discountType,
        BigDecimal discountValue,
        BigDecimal impact
) {
}

