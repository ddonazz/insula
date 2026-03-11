package it.andrea.insula.property.internal.property.mapper;

import it.andrea.insula.property.internal.address.mapper.PropertyAddressUpdateMapper;
import it.andrea.insula.property.internal.property.dto.request.PropertyUpdateDto;
import it.andrea.insula.property.internal.property.model.Property;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class PropertyUpdateMapper implements BiFunction<PropertyUpdateDto, Property, Property> {

    private final PropertyAddressUpdateMapper addressUpdateMapper;

    @Override
    public Property apply(PropertyUpdateDto dto, Property property) {
        property.setName(dto.name());
        property.setType(dto.type());
        property.setConstructionYear(dto.constructionYear());
        if (dto.address() != null && property.getAddress() != null) {
            addressUpdateMapper.apply(dto.address(), property.getAddress());
        }
        property.setAmenities(dto.amenities() != null ? new HashSet<>(dto.amenities()) : new HashSet<>());
        return property;
    }
}

