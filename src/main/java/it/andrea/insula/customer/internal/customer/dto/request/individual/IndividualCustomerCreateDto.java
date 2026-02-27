package it.andrea.insula.customer.internal.customer.dto.request.individual;

import it.andrea.insula.customer.internal.address.dto.request.AddressCreateDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record IndividualCustomerCreateDto(
        @NotBlank
        @Email
        String email,

        String phoneNumber,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotBlank
        String fiscalCode,

        LocalDate birthDate,

        String birthPlace,

        String nationality,

        @NotNull
        @Valid
        AddressCreateDto billingAddress
) {
}
