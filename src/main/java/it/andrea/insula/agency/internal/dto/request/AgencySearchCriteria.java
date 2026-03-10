package it.andrea.insula.agency.internal.dto.request;

import it.andrea.insula.agency.internal.model.AgencyStatus;

public record AgencySearchCriteria(
        String name,
        String vatNumber,
        AgencyStatus status
) {
}

