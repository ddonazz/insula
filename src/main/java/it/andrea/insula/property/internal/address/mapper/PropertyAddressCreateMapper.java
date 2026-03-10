package it.andrea.insula.property.internal.address.mapper;

import it.andrea.insula.property.internal.address.dto.request.PropertyAddressCreateDto;
import it.andrea.insula.property.internal.address.model.PropertyAddress;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PropertyAddressCreateMapper implements Function<PropertyAddressCreateDto, PropertyAddress> {

    @Override
    public PropertyAddress apply(PropertyAddressCreateDto dto) {
        PropertyAddress address = new PropertyAddress();
        address.setStreet(dto.street());
        address.setNumber(dto.number());
        address.setPostalCode(dto.postalCode());
        address.setCity(dto.city());
        address.setMunicipality(dto.municipality());
        address.setProvince(dto.province());
        address.setCountry(dto.country());
        address.setLatitude(dto.latitude());
        address.setLongitude(dto.longitude());
        address.setNotes(dto.notes());
        return address;
    }
}
