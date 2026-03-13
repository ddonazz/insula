package it.andrea.insula.customer.internal.customer.dto.request;

import it.andrea.insula.customer.internal.address.dto.request.CustomerAddressUpdateDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record IndividualCustomerUpdateDto(
        @NotBlank
        @Email
        String email,

        String phoneNumber,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        LocalDate birthDate,

        String birthPlace,

        String nationality,

        @Valid
        CustomerAddressUpdateDto billingAddress
) implements CustomerUpdateDto {
}

