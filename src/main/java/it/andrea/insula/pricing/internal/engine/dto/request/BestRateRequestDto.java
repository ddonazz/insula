package it.andrea.insula.pricing.internal.engine.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
public record BestRateRequestDto(
        @NotNull UUID priceListPublicId,
        @NotEmpty List<UUID> unitPublicIds,
        @NotNull LocalDate checkIn,
        @NotNull LocalDate checkOut,
        @Min(1) int guests,
        @NotNull LocalDate bookingDate
) {
}

