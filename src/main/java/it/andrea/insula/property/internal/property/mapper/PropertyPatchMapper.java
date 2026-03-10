package it.andrea.insula.property.internal.property.mapper;

import it.andrea.insula.property.internal.address.mapper.PropertyAddressPatchMapper;
import it.andrea.insula.property.internal.property.dto.request.PropertyPatchDto;
import it.andrea.insula.property.internal.property.model.Property;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class PropertyPatchMapper implements BiFunction<PropertyPatchDto, Property, Property> {

    private final PropertyAddressPatchMapper addressPatchMapper;

    @Override
    public Property apply(PropertyPatchDto dto, Property property) {
        if (dto.name() != null) {
            property.setName(dto.name());
        }
        if (dto.type() != null) {
            property.setType(dto.type());
        }
        if (dto.constructionYear() != null) {
            property.setConstructionYear(dto.constructionYear());
        }
        if (dto.address() != null && property.getAddress() != null) {
            addressPatchMapper.apply(dto.address(), property.getAddress());
        }
        if (dto.amenities() != null) {
            property.setAmenities(new HashSet<>(dto.amenities()));
        }
        return property;
    }
}

