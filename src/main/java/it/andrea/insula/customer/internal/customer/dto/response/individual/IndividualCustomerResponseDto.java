package it.andrea.insula.customer.internal.customer.dto.response.individual;

import it.andrea.insula.customer.internal.address.dto.respose.AddressResponseDto;
import it.andrea.insula.customer.internal.customer.model.CustomerType;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record IndividualCustomerResponseDto(
        UUID id,
        String email,
        String phoneNumber,
        UUID userId,
        CustomerType customerType,
        String firstName,
        String lastName,
        String fiscalCode,
        LocalDate birthDate,
        String birthPlace,
        String nationality,
        AddressResponseDto billingAddress
) {
}
