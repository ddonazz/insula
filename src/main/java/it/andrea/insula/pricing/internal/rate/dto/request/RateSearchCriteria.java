package it.andrea.insula.pricing.internal.rate.dto.request;

import lombok.Builder;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record RateSearchCriteria(
        UUID unitPublicId,
        UUID sourceSeasonPublicId,
        LocalDate stayDateFrom,
        LocalDate stayDateTo,
        Boolean stopSell,
        Boolean closedToArrival,
        Boolean closedToDeparture
) {
}

