package it.andrea.insula.property.internal.address.mapper;

import it.andrea.insula.property.internal.address.dto.response.PropertyAddressResponseDto;
import it.andrea.insula.property.internal.address.model.PropertyAddress;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PropertyAddressResponseMapper implements Function<PropertyAddress, PropertyAddressResponseDto> {

    @Override
    public PropertyAddressResponseDto apply(PropertyAddress address) {
        if (address == null) {
            return null;
        }
        return PropertyAddressResponseDto.builder()
                .publicId(address.getPublicId())
                .street(address.getStreet())
                .number(address.getNumber())
                .postalCode(address.getPostalCode())
                .city(address.getCity())
                .municipality(address.getMunicipality())
                .province(address.getProvince())
                .country(address.getCountry())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .notes(address.getNotes())
                .build();
    }
}
