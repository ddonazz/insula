package it.andrea.insula.owner.internal.owner.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record BusinessOwnerPatchDto(
        @Email
        String email,

        String phoneNumber,

        String companyName,

        String fiscalCode,

        String vatNumber,

        @Size(max = 7)
        String sdiCode,

        @Email
        String pecEmail,

        OwnerAddressDto address,

        BankAccountDto bankAccount
) implements OwnerPatchDto {
}

