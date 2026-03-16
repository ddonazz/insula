package it.andrea.insula.pricing.internal.promotion.dto.request;

import it.andrea.insula.pricing.internal.core.AdjustmentType;
import it.andrea.insula.pricing.internal.promotion.model.PromotionStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record PromotionPatchDto(
        UUID priceListPublicId,
        String name,
        String description,
        LocalDate bookingFrom,
        LocalDate bookingTo,
        LocalDate stayFrom,
        LocalDate stayTo,
        Integer minNights,
        AdjustmentType discountType,
        BigDecimal discountValue,
        Integer maxUsages,
        PromotionStatus status
) {
}

