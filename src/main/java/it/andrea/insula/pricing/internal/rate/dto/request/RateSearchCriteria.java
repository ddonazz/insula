package it.andrea.insula.pricing.internal.rate.dto.request;

import java.time.LocalDate;
import java.util.UUID;

public record RateSearchCriteria(
        UUID unitPublicId,
        LocalDate startDateFrom,
        LocalDate startDateTo,
        Boolean stopSell
) {
}

