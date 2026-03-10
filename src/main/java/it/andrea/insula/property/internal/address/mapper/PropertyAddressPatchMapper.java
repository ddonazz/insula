package it.andrea.insula.property.internal.address.mapper;

import it.andrea.insula.property.internal.address.dto.request.PropertyAddressPatchDto;
import it.andrea.insula.property.internal.address.model.PropertyAddress;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class PropertyAddressPatchMapper implements BiFunction<PropertyAddressPatchDto, PropertyAddress, PropertyAddress> {

    @Override
    public PropertyAddress apply(PropertyAddressPatchDto dto, PropertyAddress address) {
        if (dto == null) {
            return address;
        }
        if (dto.street() != null) {
            address.setStreet(dto.street());
        }
        if (dto.number() != null) {
            address.setNumber(dto.number());
        }
        if (dto.postalCode() != null) {
            address.setPostalCode(dto.postalCode());
        }
        if (dto.city() != null) {
            address.setCity(dto.city());
        }
        if (dto.municipality() != null) {
            address.setMunicipality(dto.municipality());
        }
        if (dto.province() != null) {
            address.setProvince(dto.province());
        }
        if (dto.country() != null) {
            address.setCountry(dto.country());
        }
        if (dto.latitude() != null) {
            address.setLatitude(dto.latitude());
        }
        if (dto.longitude() != null) {
            address.setLongitude(dto.longitude());
        }
        if (dto.notes() != null) {
            address.setNotes(dto.notes());
        }
        return address;
    }
}
