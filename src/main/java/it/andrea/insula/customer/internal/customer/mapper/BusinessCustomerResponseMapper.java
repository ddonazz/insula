package it.andrea.insula.customer.internal.customer.mapper;

import it.andrea.insula.customer.internal.address.mapper.CustomerAddressResponseMapper;
import it.andrea.insula.customer.internal.customer.dto.response.BusinessCustomerResponseDto;
import it.andrea.insula.customer.internal.customer.dto.response.CustomerContactResponseDto;
import it.andrea.insula.customer.internal.customer.model.BusinessCustomer;
import it.andrea.insula.customer.internal.customer.model.CustomerContact;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BusinessCustomerResponseMapper implements Function<BusinessCustomer, BusinessCustomerResponseDto> {

    private final CustomerAddressResponseMapper addressMapper;

    @Override
    public BusinessCustomerResponseDto apply(BusinessCustomer customer) {
        return BusinessCustomerResponseDto.builder()
                .publicId(customer.getPublicId())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .customerType(customer.getCustomerType())
                .companyName(customer.getCompanyName())
                .vatNumber(customer.getVatNumber())
                .fiscalCode(customer.getFiscalCode())
                .legalAddress(addressMapper.apply(customer.getLegalAddress()))
                .billingAddress(addressMapper.apply(customer.getBillingAddress()))
                .operationalAddresses(customer.getOperationalAddresses() != null
                        ? customer.getOperationalAddresses().stream().map(addressMapper).collect(Collectors.toSet())
                        : Collections.emptySet())
                .sdiCode(customer.getSdiCode())
                .pecEmail(customer.getPecEmail())
                .contacts(mapContacts(customer.getContacts()))
                .build();
    }

    private Set<CustomerContactResponseDto> mapContacts(Set<CustomerContact> contacts) {
        if (contacts == null) {
            return Collections.emptySet();
        }
        return contacts.stream()
                .map(c -> CustomerContactResponseDto.builder()
                        .publicId(c.getPublicId())
                        .userId(c.getUserPublicId())
                        .firstName(c.getFirstName())
                        .lastName(c.getLastName())
                        .email(c.getEmail())
                        .jobTitle(c.getJobTitle())
                        .build())
                .collect(Collectors.toSet());
    }
}
