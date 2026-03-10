package it.andrea.insula.property.internal.property.mapper;

import it.andrea.insula.core.dto.EnumTranslator;
import it.andrea.insula.property.internal.address.mapper.PropertyAddressResponseMapper;
import it.andrea.insula.property.internal.property.dto.response.PropertyResponseDto;
import it.andrea.insula.property.internal.property.model.Property;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class PropertyResponseMapper implements Function<Property, PropertyResponseDto> {

    private final EnumTranslator enumTranslator;
    private final PropertyAddressResponseMapper addressResponseMapper;

    @Override
    public PropertyResponseDto apply(Property property) {
        return PropertyResponseDto.builder()
                .publicId(property.getPublicId())
                .name(property.getName())
                .type(enumTranslator.translate(property.getType()))
                .address(addressResponseMapper.apply(property.getAddress()))
                .constructionYear(property.getConstructionYear())
                .amenities(enumTranslator.translateAll(property.getAmenities()))
                .unitCount(property.getUnits() != null ? property.getUnits().size() : 0)
                .build();
    }
}
