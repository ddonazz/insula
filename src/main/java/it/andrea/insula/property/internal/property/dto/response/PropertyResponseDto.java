package it.andrea.insula.property.internal.property.dto.response;

import it.andrea.insula.core.dto.TranslatedEnum;
import it.andrea.insula.property.internal.address.dto.response.PropertyAddressResponseDto;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record PropertyResponseDto(
        UUID publicId,
        String name,
        TranslatedEnum type,
        PropertyAddressResponseDto address,
        Integer constructionYear,
        Set<TranslatedEnum> amenities,
        int unitCount
) {
}

