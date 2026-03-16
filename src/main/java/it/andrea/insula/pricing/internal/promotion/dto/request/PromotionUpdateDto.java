package it.andrea.insula.pricing.internal.promotion.dto.request;

import it.andrea.insula.pricing.internal.core.AdjustmentType;
import it.andrea.insula.pricing.internal.promotion.model.PromotionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record PromotionUpdateDto(
        UUID priceListPublicId,
        @NotBlank
        String name,
        String description,
        @NotNull
        LocalDate bookingFrom,
        LocalDate bookingTo,
        @NotNull
        LocalDate stayFrom,
        @NotNull
        LocalDate stayTo,
        Integer minNights,
        @NotNull
        AdjustmentType discountType,
        @NotNull
        BigDecimal discountValue,
        Integer maxUsages,
        @NotNull
        PromotionStatus status
) {
}

