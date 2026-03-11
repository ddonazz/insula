package it.andrea.insula.customer.internal.address.mapper;

import it.andrea.insula.customer.internal.address.dto.request.CustomerAddressUpdateDto;
import it.andrea.insula.customer.internal.address.model.CustomerAddress;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class CustomerAddressUpdateMapper implements BiFunction<CustomerAddressUpdateDto, CustomerAddress, CustomerAddress> {

    @Override
    public CustomerAddress apply(CustomerAddressUpdateDto dto, CustomerAddress address) {
        address.setStreet(dto.street());
        address.setNumber(dto.number());
        address.setPostalCode(dto.postalCode());
        address.setCity(dto.city());
        address.setProvince(dto.province());
        address.setCountry(dto.country());
        return address;
    }
}

