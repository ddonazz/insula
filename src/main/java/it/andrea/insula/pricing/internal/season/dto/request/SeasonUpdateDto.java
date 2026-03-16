package it.andrea.insula.pricing.internal.season.dto.request;

import it.andrea.insula.pricing.internal.season.model.SeasonStatus;
import it.andrea.insula.pricing.internal.season.model.SeasonType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record SeasonUpdateDto(
        @NotBlank
        String name,
        @NotNull
        SeasonType seasonType,
        @NotNull
        LocalDate startDate,
        @NotNull
        LocalDate endDate,
        int priority,
        @NotNull
        SeasonStatus status
) {
}

