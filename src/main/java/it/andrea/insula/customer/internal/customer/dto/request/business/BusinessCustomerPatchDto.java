package it.andrea.insula.customer.internal.customer.dto.request.business;

import it.andrea.insula.customer.internal.address.dto.request.CustomerAddressPatchDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record BusinessCustomerPatchDto(
        @Email
        String email,

        String phoneNumber,

        String companyName,

        String vatNumber,

        String fiscalCode,

        @Size(max = 7)
        String sdiCode,

        @Email
        String pecEmail,

        @Valid
        CustomerAddressPatchDto legalAddress,

        @Valid
        CustomerAddressPatchDto billingAddress
) {
}

