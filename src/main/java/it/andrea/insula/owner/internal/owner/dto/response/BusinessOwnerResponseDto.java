package it.andrea.insula.owner.internal.owner.dto.response;

import it.andrea.insula.core.dto.TranslatedEnum;
import lombok.Builder;

import java.util.UUID;

@Builder
public record BusinessOwnerResponseDto(
        UUID publicId,
        TranslatedEnum ownerType,
        TranslatedEnum status,
        String email,
        String phoneNumber,
        String companyName,
        String fiscalCode,
        String vatNumber,
        String sdiCode,
        String pecEmail,
        OwnerResponseDto.OwnerAddressResponseDto address,
        OwnerResponseDto.BankAccountResponseDto bankAccount
) implements OwnerResponseDto {
}

