package it.andrea.insula.owner.internal.owner.dto.response;

import it.andrea.insula.core.dto.TranslatedEnum;
import lombok.Builder;

import java.util.UUID;

@Builder
public record IndividualOwnerResponseDto(
        UUID publicId,
        TranslatedEnum ownerType,
        TranslatedEnum status,
        String email,
        String phoneNumber,
        String firstName,
        String lastName,
        String fiscalCode,
        OwnerResponseDto.OwnerAddressResponseDto address,
        OwnerResponseDto.BankAccountResponseDto bankAccount
) implements OwnerResponseDto {
}

