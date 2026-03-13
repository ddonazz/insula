package it.andrea.insula.customer.internal.customer.dto.request;

import it.andrea.insula.customer.internal.address.dto.request.CustomerAddressPatchDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

import java.time.LocalDate;

public record IndividualCustomerPatchDto(
        @Email
        String email,

        String phoneNumber,

        String firstName,

        String lastName,

        LocalDate birthDate,

        String birthPlace,

        String nationality,

        @Valid
        CustomerAddressPatchDto billingAddress
) implements CustomerPatchDto {
}

