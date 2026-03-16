package it.andrea.insula.pricing.internal.promotion.dto.request;

import it.andrea.insula.pricing.internal.promotion.model.PromotionStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record PromotionSearchCriteria(
        UUID priceListPublicId,
        String name,
        PromotionStatus status,
        LocalDate bookingDate,
        LocalDate stayDate
) {
}

