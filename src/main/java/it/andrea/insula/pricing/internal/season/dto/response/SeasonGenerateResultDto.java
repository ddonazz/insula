package it.andrea.insula.pricing.internal.season.dto.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record SeasonGenerateResultDto(
        int generated,
        int skippedManual,
        int unitsProcessed,
        LocalDate from,
        LocalDate to
) {
}

