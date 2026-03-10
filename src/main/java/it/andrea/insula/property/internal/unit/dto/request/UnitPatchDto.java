package it.andrea.insula.property.internal.unit.dto.request;

import it.andrea.insula.property.internal.unit.model.UnitAmenity;
import it.andrea.insula.property.internal.unit.model.UnitType;
import jakarta.validation.Valid;

import java.util.Set;

public record UnitPatchDto(
        String internalName,

        UnitType type,

        String floor,
        String internalNumber,

        Double totalAreaMq,
        Double walkableAreaMq,
        Integer roomCount,
        Integer bedroomCount,
        Integer bathroomCount,
        Integer maxOccupancy,

        String regionalIdentifierCode,

        @Valid
        EnergyCertificatePatchDto energyCertificate,

        Set<UnitAmenity> amenities
) {
}

