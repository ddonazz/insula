package it.andrea.insula.property.internal.unit.dto.response;

import it.andrea.insula.core.dto.TranslatedEnum;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record UnitResponseDto(
        UUID publicId,
        UUID propertyPublicId,
        String internalName,
        TranslatedEnum type,
        String floor,
        String internalNumber,
        Double totalAreaMq,
        Double walkableAreaMq,
        Integer roomCount,
        Integer bedroomCount,
        Integer bathroomCount,
        Integer maxOccupancy,
        String regionalIdentifierCode,
        EnergyCertificateResponseDto energyCertificate,
        Set<CadastralDataResponseDto> cadastralData,
        Set<TranslatedEnum> amenities
) {
}

