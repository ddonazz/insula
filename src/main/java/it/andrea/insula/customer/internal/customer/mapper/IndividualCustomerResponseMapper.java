package it.andrea.insula.customer.internal.customer.mapper;

import it.andrea.insula.customer.internal.address.mapper.AddressToAddressResponseDtoMapper;
import it.andrea.insula.customer.internal.customer.dto.response.individual.IndividualCustomerResponseDto;
import it.andrea.insula.customer.internal.customer.model.IndividualCustomer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class IndividualCustomerResponseMapper implements Function<IndividualCustomer, IndividualCustomerResponseDto> {

    private final AddressToAddressResponseDtoMapper addressMapper;

    @Override
    public IndividualCustomerResponseDto apply(IndividualCustomer customer) {
        return IndividualCustomerResponseDto.builder()
                .id(customer.getPublicId())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .userId(customer.getUserPublicId())
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

