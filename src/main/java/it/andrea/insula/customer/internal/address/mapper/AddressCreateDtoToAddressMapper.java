package it.andrea.insula.customer.internal.address.mapper;

import it.andrea.insula.customer.internal.address.dto.request.AddressCreateDto;
import it.andrea.insula.customer.internal.address.model.Address;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class AddressCreateDtoToAddressMapper implements Function<AddressCreateDto, Address> {

    @Override
    public Address apply(AddressCreateDto dto) {
        return Address.builder()
                .street(dto.street())
                .number(dto.number())
                .postalCode(dto.postalCode())
                .city(dto.city())
                .province(dto.province())
                .country(dto.country())
                .build();
    }
}

