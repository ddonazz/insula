package it.andrea.insula.property.internal.property.dto.request;

import it.andrea.insula.property.internal.property.model.PropertyType;

public record PropertySearchCriteria(
        String name,
        PropertyType type,
        String city,
        String province
) {
}

