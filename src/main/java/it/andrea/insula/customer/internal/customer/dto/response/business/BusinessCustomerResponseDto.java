package it.andrea.insula.customer.internal.customer.dto.response.business;

import it.andrea.insula.customer.internal.address.dto.respose.AddressResponseDto;
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
        AddressResponseDto legalAddress,
        AddressResponseDto billingAddress,
        Set<AddressResponseDto> operationalAddresses,
        String sdiCode,
        String pecEmail,
        Set<CustomerContactResponseDto> contacts
) {
}
