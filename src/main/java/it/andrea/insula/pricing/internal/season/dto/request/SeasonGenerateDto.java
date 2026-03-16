package it.andrea.insula.pricing.internal.season.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
public record SeasonGenerateDto(
        @NotEmpty
        List<UUID> unitPublicIds,

        @NotNull
        @Positive
        BigDecimal pricePerNight,

        @Positive
        BigDecimal extraGuestPrice,

        @Positive
        Integer minStay,

        @Positive
        Integer maxStay,

        boolean overwriteManual
) {
}

