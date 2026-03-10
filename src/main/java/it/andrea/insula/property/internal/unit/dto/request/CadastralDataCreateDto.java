package it.andrea.insula.property.internal.unit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastralDataCreateDto(
        @NotBlank
        String sheet,

        @NotBlank
        String parcel,

        @NotBlank
        String subordinate,

        @NotBlank
        @Size(max = 10)
        String category,

        @Size(max = 10)
        String buildingClass,

        String consistency,

        Double cadastralIncome
) {
}

