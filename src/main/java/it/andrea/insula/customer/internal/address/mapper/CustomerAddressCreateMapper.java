package it.andrea.insula.customer.internal.address.mapper;

import it.andrea.insula.customer.internal.address.dto.request.CustomerAddressCreateDto;
import it.andrea.insula.customer.internal.address.model.CustomerAddress;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CustomerAddressCreateMapper implements Function<CustomerAddressCreateDto, CustomerAddress> {

    @Override
    public CustomerAddress apply(CustomerAddressCreateDto dto) {
        return CustomerAddress.builder()
                .street(dto.street())
                .number(dto.number())
                .postalCode(dto.postalCode())
                .city(dto.city())
                .province(dto.province())
                .country(dto.country())
                .build();
    }
}
