package it.andrea.insula.customer.internal.customer.mapper;

import it.andrea.insula.customer.internal.address.mapper.CustomerAddressCreateMapper;
import it.andrea.insula.customer.internal.customer.dto.request.individual.IndividualCustomerCreateDto;
import it.andrea.insula.customer.internal.customer.model.IndividualCustomer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class IndividualCustomerCreateMapper implements Function<IndividualCustomerCreateDto, IndividualCustomer> {

    private final CustomerAddressCreateMapper addressMapper;

    @Override
    public IndividualCustomer apply(IndividualCustomerCreateDto dto) {
        IndividualCustomer customer = new IndividualCustomer();
        customer.setEmail(dto.email());
        customer.setPhoneNumber(dto.phoneNumber());
        customer.setFirstName(dto.firstName());
        customer.setLastName(dto.lastName());
        customer.setFiscalCode(dto.fiscalCode());
        customer.setBirthDate(dto.birthDate());
        customer.setBirthPlace(dto.birthPlace());
        customer.setNationality(dto.nationality());
        if (dto.billingAddress() != null) {
            customer.setBillingAddress(addressMapper.apply(dto.billingAddress()));
        }
        return customer;
    }
}

