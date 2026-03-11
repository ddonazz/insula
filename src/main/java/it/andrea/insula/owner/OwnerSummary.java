package it.andrea.insula.owner;

import lombok.Builder;

import java.util.UUID;

/**
 * Lightweight, cross-module representation of an Owner.
 * Exposed by {@link OwnerQueryService} for inter-module queries.
 */
@Builder
public record OwnerSummary(
        UUID publicId,
        String type,
        String displayName,
        String email,
        String fiscalCode
) {
}

