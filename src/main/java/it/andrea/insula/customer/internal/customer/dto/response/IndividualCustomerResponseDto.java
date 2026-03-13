package it.andrea.insula.customer.internal.customer.dto.response;

import it.andrea.insula.customer.internal.address.dto.respose.CustomerAddressResponseDto;
import it.andrea.insula.customer.internal.customer.model.CustomerType;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record IndividualCustomerResponseDto(
        UUID publicId,
        CustomerType customerType,
        String email,
        String phoneNumber,
        UUID userPublicId,
        String firstName,
        String lastName,
        String fiscalCode,
        LocalDate birthDate,
        String birthPlace,
        String nationality,
        CustomerAddressResponseDto billingAddress
) implements CustomerResponseDto {
}

