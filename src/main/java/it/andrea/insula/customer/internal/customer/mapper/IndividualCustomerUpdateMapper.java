package it.andrea.insula.customer.internal.customer.mapper;

import it.andrea.insula.customer.internal.address.mapper.CustomerAddressUpdateMapper;
import it.andrea.insula.customer.internal.customer.dto.request.individual.IndividualCustomerUpdateDto;
import it.andrea.insula.customer.internal.customer.model.IndividualCustomer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class IndividualCustomerUpdateMapper implements BiFunction<IndividualCustomerUpdateDto, IndividualCustomer, IndividualCustomer> {

    private final CustomerAddressUpdateMapper addressUpdateMapper;

    @Override
    public IndividualCustomer apply(IndividualCustomerUpdateDto dto, IndividualCustomer customer) {
        customer.setEmail(dto.email());
        customer.setPhoneNumber(dto.phoneNumber());
        customer.setFirstName(dto.firstName());
        customer.setLastName(dto.lastName());
        customer.setBirthDate(dto.birthDate());
        customer.setBirthPlace(dto.birthPlace());
        customer.setNationality(dto.nationality());
        if (dto.billingAddress() != null && customer.getBillingAddress() != null) {
            addressUpdateMapper.apply(dto.billingAddress(), customer.getBillingAddress());
        }
        return customer;
    }
}

