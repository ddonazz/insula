package it.andrea.insula.owner.internal.owner.mapper;

import it.andrea.insula.core.dto.EnumTranslator;
import it.andrea.insula.owner.internal.owner.dto.response.OwnerResponseDto;
import it.andrea.insula.owner.internal.owner.model.BankAccount;
import it.andrea.insula.owner.internal.owner.model.Owner;
import it.andrea.insula.owner.internal.owner.model.OwnerAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class OwnerResponseMapper implements Function<Owner, OwnerResponseDto> {

    private final EnumTranslator enumTranslator;

    @Override
    public OwnerResponseDto apply(Owner owner) {
        return OwnerResponseDto.builder()
                .publicId(owner.getPublicId())
                .type(enumTranslator.translate(owner.getType()))
                .status(enumTranslator.translate(owner.getStatus()))
                .email(owner.getEmail())
                .phoneNumber(owner.getPhoneNumber())
                .firstName(owner.getFirstName())
                .lastName(owner.getLastName())
                .companyName(owner.getCompanyName())
                .fiscalCode(owner.getFiscalCode())
                .vatNumber(owner.getVatNumber())
                .sdiCode(owner.getSdiCode())
                .pecEmail(owner.getPecEmail())
                .address(mapAddress(owner.getAddress()))
                .bankAccount(mapBankAccount(owner.getBankAccount()))
                .build();
    }

    private OwnerResponseDto.OwnerAddressResponseDto mapAddress(OwnerAddress address) {
        if (address == null) return null;
        return OwnerResponseDto.OwnerAddressResponseDto.builder()
                .street(address.getStreet())
                .streetNumber(address.getStreetNumber())
                .zipCode(address.getZipCode())
                .city(address.getCity())
                .province(address.getProvince())
                .country(address.getCountry())
                .build();
    }

    private OwnerResponseDto.BankAccountResponseDto mapBankAccount(BankAccount bank) {
        if (bank == null) return null;
        return OwnerResponseDto.BankAccountResponseDto.builder()
                .iban(bank.getIban())
                .swiftBic(bank.getSwiftBic())
                .extraEuAccountNumber(bank.getExtraEuAccountNumber())
                .routingCode(bank.getRoutingCode())
                .bankName(bank.getBankName())
                .bankCountryCode(bank.getBankCountryCode())
                .holderName(bank.getHolderName())
                .build();
    }
}

