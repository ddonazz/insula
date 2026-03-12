package it.andrea.insula.pricing;

import lombok.Builder;

import java.util.UUID;

/**
 * Lightweight, cross-module representation of a PriceList.
 * Exposed by {@link PricingQueryService} for inter-module queries.
 */
@Builder
public record PriceListSummary(
        UUID publicId,
        String name,
        String currency,
        boolean isDefault,
        String status
) {
}

