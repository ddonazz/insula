package it.andrea.insula.pricing.internal.engine.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record AvailabilityQueryDto(
        @NotNull UUID priceListPublicId,
        @NotNull UUID unitPublicId,
        @NotNull LocalDate from,
        @NotNull LocalDate to,
        UUID ratePlanPublicId,
        @Min(1) int guests
) {
}

