package it.andrea.insula.customer.internal.customer.mapper;

import it.andrea.insula.customer.internal.address.mapper.CustomerAddressResponseMapper;
import it.andrea.insula.customer.internal.customer.dto.response.IndividualCustomerResponseDto;
import it.andrea.insula.customer.internal.customer.model.IndividualCustomer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class IndividualCustomerResponseMapper implements Function<IndividualCustomer, IndividualCustomerResponseDto> {

    private final CustomerAddressResponseMapper addressMapper;

    @Override
    public IndividualCustomerResponseDto apply(IndividualCustomer customer) {
        return IndividualCustomerResponseDto.builder()
                .publicId(customer.getPublicId())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .userPublicId(customer.getUserPublicId())
                .customerType(customer.getCustomerType())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .fiscalCode(customer.getFiscalCode())
                .birthDate(customer.getBirthDate())
                .birthPlace(customer.getBirthPlace())
                .nationality(customer.getNationality())
                .billingAddress(addressMapper.apply(customer.getBillingAddress()))
                .build();
    }
}
