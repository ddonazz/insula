package it.andrea.insula.property.internal.unit.dto.request;

import it.andrea.insula.property.internal.unit.model.UnitAmenity;
import it.andrea.insula.property.internal.unit.model.UnitType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record UnitUpdateDto(
        @NotBlank
        String internalName,

        @NotNull
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
        EnergyCertificateUpdateDto energyCertificate,

        Set<UnitAmenity> amenities
) {
}

