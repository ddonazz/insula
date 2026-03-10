package it.andrea.insula.customer.internal.address.mapper;

import it.andrea.insula.customer.internal.address.dto.respose.AddressResponseDto;
import it.andrea.insula.customer.internal.address.model.Address;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class AddressToAddressResponseDtoMapper implements Function<Address, AddressResponseDto> {

    @Override
    public AddressResponseDto apply(Address address) {
        if (address == null) {
            return null;
        }
        return AddressResponseDto.builder()
                .id(address.getId())
                .street(address.getStreet())
                .number(address.getNumber())
                .postalCode(address.getPostalCode())
                .city(address.getCity())
                .province(address.getProvince())
                .country(address.getCountry())
                .build();
    }
}

