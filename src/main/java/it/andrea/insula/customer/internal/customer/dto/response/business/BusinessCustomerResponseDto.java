package it.andrea.insula.customer.internal.customer.dto.response.business;

import it.andrea.insula.customer.internal.address.dto.respose.CustomerAddressResponseDto;
import it.andrea.insula.customer.internal.customer.model.CustomerType;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record BusinessCustomerResponseDto(
        UUID publicId,
        String email,
        String phoneNumber,
        CustomerType customerType,
        String companyName,
        String vatNumber,
        String fiscalCode,
        CustomerAddressResponseDto legalAddress,
        CustomerAddressResponseDto billingAddress,
        Set<CustomerAddressResponseDto> operationalAddresses,
        String sdiCode,
        String pecEmail,
        Set<CustomerContactResponseDto> contacts
) {
}
