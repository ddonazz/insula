package it.andrea.insula.property.internal.address.mapper;

import it.andrea.insula.property.internal.address.dto.request.PropertyAddressUpdateDto;
import it.andrea.insula.property.internal.address.model.PropertyAddress;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class PropertyAddressUpdateMapper implements BiFunction<PropertyAddressUpdateDto, PropertyAddress, PropertyAddress> {

    @Override
    public PropertyAddress apply(PropertyAddressUpdateDto dto, PropertyAddress address) {
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

