package it.andrea.insula.customer.internal.address.mapper;

import it.andrea.insula.customer.internal.address.dto.respose.CustomerAddressResponseDto;
import it.andrea.insula.customer.internal.address.model.CustomerAddress;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CustomerAddressResponseMapper implements Function<CustomerAddress, CustomerAddressResponseDto> {

    @Override
    public CustomerAddressResponseDto apply(CustomerAddress address) {
        if (address == null) {
            return null;
        }
        return CustomerAddressResponseDto.builder()
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
