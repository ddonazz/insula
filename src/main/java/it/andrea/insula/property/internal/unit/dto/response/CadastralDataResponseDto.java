package it.andrea.insula.property.internal.unit.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CadastralDataResponseDto(
        UUID publicId,
        String sheet,
        String parcel,
        String subordinate,
        String category,
        String buildingClass,
        String consistency,
        Double cadastralIncome
) {
}

