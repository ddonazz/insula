package it.andrea.insula.customer.internal.customer.dto.response;

import it.andrea.insula.customer.internal.address.dto.respose.CustomerAddressResponseDto;
import it.andrea.insula.customer.internal.customer.model.CustomerType;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Builder
public record BusinessCustomerResponseDto(
        UUID publicId,
        CustomerType customerType,
        String email,
        String phoneNumber,
        String displayName,
        String companyName,
        String vatNumber,
        String fiscalCode,
        CustomerAddressResponseDto legalAddress,
        CustomerAddressResponseDto billingAddress,
        Set<CustomerAddressResponseDto> operationalAddresses,
        String sdiCode,
        String pecEmail,
        Set<CustomerContactResponseDto> contacts
) implements CustomerResponseDto {
}
