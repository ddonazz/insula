package it.andrea.insula.customer.internal.address.mapper;

import it.andrea.insula.customer.internal.address.dto.request.AddressUpdateDto;
import it.andrea.insula.customer.internal.address.model.Address;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class AddressPatchMapper implements BiFunction<AddressUpdateDto, Address, Address> {

    @Override
    public Address apply(AddressUpdateDto dto, Address address) {
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
        if (dto.province() != null) {
            address.setProvince(dto.province());
        }
        if (dto.country() != null) {
            address.setCountry(dto.country());
        }
        return address;
    }
}

