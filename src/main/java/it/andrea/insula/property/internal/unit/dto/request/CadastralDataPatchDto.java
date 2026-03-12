package it.andrea.insula.property.internal.unit.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CadastralDataPatchDto(
        String sheet,
        String parcel,
        String subordinate,
        @Size(max = 10)
        String category,
        @Size(max = 10)
        String buildingClass,
        String consistency,
        Double cadastralIncome
) {
}

