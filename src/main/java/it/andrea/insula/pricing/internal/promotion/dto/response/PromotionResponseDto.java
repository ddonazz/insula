package it.andrea.insula.pricing.internal.promotion.dto.response;

import it.andrea.insula.core.dto.TranslatedEnum;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record PromotionResponseDto(
        UUID publicId,
        UUID priceListPublicId,
        String priceListName,
        String name,
        String description,
        LocalDate bookingFrom,
        LocalDate bookingTo,
        LocalDate stayFrom,
        LocalDate stayTo,
        Integer minNights,
        TranslatedEnum discountType,
        BigDecimal discountValue,
        Integer maxUsages,
        int currentUsages,
        TranslatedEnum status
) {
}

