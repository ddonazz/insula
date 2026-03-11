package it.andrea.insula.property;

import lombok.Builder;

import java.util.UUID;

/**
 * Lightweight, cross-module representation of a Unit.
 * Exposed by {@link PropertyQueryService} for inter-module queries.
 */
@Builder
public record UnitSummary(
        UUID publicId,
        UUID propertyPublicId,
        String propertyName,
        String internalName,
        String type,
        String floor,
        String internalNumber
) {
}

