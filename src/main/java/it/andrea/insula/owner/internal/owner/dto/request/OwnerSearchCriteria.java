package it.andrea.insula.owner.internal.owner.dto.request;

import it.andrea.insula.owner.internal.owner.model.OwnerType;

public record OwnerSearchCriteria(
        String name,
        String email,
        String fiscalCode,
        OwnerType ownerType
) {
}

