package it.andrea.insula.property.internal.property.dto.request;

import it.andrea.insula.property.internal.address.dto.request.PropertyAddressPatchDto;
import it.andrea.insula.property.internal.property.model.BuildingAmenity;
import it.andrea.insula.property.internal.property.model.PropertyType;
import jakarta.validation.Valid;

import java.util.Set;

public record PropertyPatchDto(
        String name,

        PropertyType type,

        @Valid
        PropertyAddressPatchDto address,

        Integer constructionYear,

        Set<BuildingAmenity> amenities
) {
}

