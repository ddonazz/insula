package it.andrea.insula.property.internal.property.mapper;

import it.andrea.insula.property.internal.address.mapper.PropertyAddressCreateMapper;
import it.andrea.insula.property.internal.property.dto.request.PropertyCreateDto;
import it.andrea.insula.property.internal.property.model.Property;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class PropertyCreateMapper implements Function<PropertyCreateDto, Property> {

    private final PropertyAddressCreateMapper addressCreateMapper;

    @Override
    public Property apply(PropertyCreateDto dto) {
        Property property = new Property();
        property.setName(dto.name());
        property.setType(dto.type());
        property.setConstructionYear(dto.constructionYear());

        if (dto.address() != null) {
            property.setAddress(addressCreateMapper.apply(dto.address()));
        }

        if (dto.amenities() != null) {
            property.setAmenities(new HashSet<>(dto.amenities()));
        }

        return property;
    }
}

