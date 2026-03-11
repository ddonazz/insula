package it.andrea.insula.owner.internal.owner.dto.request;

import it.andrea.insula.owner.internal.owner.model.OwnerType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record OwnerPatchDto(
        OwnerType type,

        @Email
        String email,

        String phoneNumber,

        String firstName,
        String lastName,

        String companyName,

        String fiscalCode,

        String vatNumber,

        @Size(max = 7)
        String sdiCode,

        @Email
        String pecEmail,

        OwnerAddressDto address,

        BankAccountDto bankAccount
) {
}

