package it.andrea.insula.customer.internal.customer.dto.request.business;

import it.andrea.insula.customer.internal.address.dto.request.AddressUpdateDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BusinessCustomerUpdateDto(
        @NotBlank
        @Email
        String email,

        String phoneNumber,

        @NotBlank
        String companyName,

        @NotBlank
        String vatNumber,

        String fiscalCode,

        @Size(max = 7)
        String sdiCode,

        @Email
        String pecEmail,

        @NotNull
        @Valid
        AddressUpdateDto legalAddress,

        @NotNull
        @Valid
        AddressUpdateDto billingAddress
) {
}
