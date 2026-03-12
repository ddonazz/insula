package it.andrea.insula.pricing.internal.pricelist.dto.request;

import it.andrea.insula.pricing.internal.pricelist.model.PriceListStatus;

public record PriceListSearchCriteria(
        String name,
        String currency,
        PriceListStatus status,
        Boolean isDefault
) {
}

