package it.andrea.insula.pricing.internal.season.dto.request;

import it.andrea.insula.pricing.internal.season.model.SeasonStatus;
import it.andrea.insula.pricing.internal.season.model.SeasonType;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record SeasonPatchDto(
        String name,
        SeasonType seasonType,
        LocalDate startDate,
        LocalDate endDate,
        Integer priority,
        SeasonStatus status
) {
}

