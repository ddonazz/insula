package it.andrea.insula.pricing.internal.season.dto.request;

import it.andrea.insula.pricing.internal.season.model.SeasonStatus;
import it.andrea.insula.pricing.internal.season.model.SeasonType;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record SeasonSearchCriteria(
        String name,
        SeasonType seasonType,
        SeasonStatus status,
        LocalDate from,
        LocalDate to
) {
}

