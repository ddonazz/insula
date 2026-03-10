package it.andrea.insula.property.internal.property.dto.request;

import it.andrea.insula.property.internal.address.dto.request.PropertyAddressCreateDto;
import it.andrea.insula.property.internal.property.model.BuildingAmenity;
import it.andrea.insula.property.internal.property.model.PropertyType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record PropertyCreateDto(
        @NotBlank
        String name,

        @NotNull
        PropertyType type,

        @Valid
        PropertyAddressCreateDto address,

        Integer constructionYear,

        Set<BuildingAmenity> amenities
) {
}

