package it.andrea.insula.customer.internal.customer.mapper;

import it.andrea.insula.customer.internal.address.mapper.CustomerAddressPatchMapper;
import it.andrea.insula.customer.internal.customer.dto.request.IndividualCustomerPatchDto;
import it.andrea.insula.customer.internal.customer.model.IndividualCustomer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class IndividualCustomerPatchMapper implements BiFunction<IndividualCustomerPatchDto, IndividualCustomer, IndividualCustomer> {

    private final CustomerAddressPatchMapper addressPatchMapper;

    @Override
    public IndividualCustomer apply(IndividualCustomerPatchDto dto, IndividualCustomer customer) {
        if (dto.email() != null) {
            customer.setEmail(dto.email());
        }
        if (dto.phoneNumber() != null) {
            customer.setPhoneNumber(dto.phoneNumber());
        }
        if (dto.firstName() != null) {
            customer.setFirstName(dto.firstName());
        }
        if (dto.lastName() != null) {
            customer.setLastName(dto.lastName());
        }
        if (dto.birthDate() != null) {
            customer.setBirthDate(dto.birthDate());
        }
        if (dto.birthPlace() != null) {
            customer.setBirthPlace(dto.birthPlace());
        }
        if (dto.nationality() != null) {
            customer.setNationality(dto.nationality());
        }
        if (dto.billingAddress() != null && customer.getBillingAddress() != null) {
            addressPatchMapper.apply(dto.billingAddress(), customer.getBillingAddress());
        }
        return customer;
    }
}
