package it.andrea.insula.owner.internal.owner.dto.request;

import jakarta.validation.constraints.Size;

public record BankAccountDto(
        @Size(max = 34)
        String iban,

        @Size(max = 11)
        String swiftBic,

        String extraEuAccountNumber,

        String routingCode,

        String bankName,

        @Size(max = 2)
        String bankCountryCode,

        String holderName
) {
}

