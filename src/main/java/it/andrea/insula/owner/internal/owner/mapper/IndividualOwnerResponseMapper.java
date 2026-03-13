package it.andrea.insula.owner.internal.owner.mapper;

import it.andrea.insula.core.dto.EnumTranslator;
import it.andrea.insula.owner.internal.owner.dto.response.IndividualOwnerResponseDto;
import it.andrea.insula.owner.internal.owner.dto.response.OwnerResponseDto;
import it.andrea.insula.owner.internal.owner.model.BankAccount;
import it.andrea.insula.owner.internal.owner.model.IndividualOwner;
import it.andrea.insula.owner.internal.owner.model.OwnerAddress;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class IndividualOwnerResponseMapper implements Function<IndividualOwner, IndividualOwnerResponseDto> {

    private final EnumTranslator enumTranslator;

    @Override
    public IndividualOwnerResponseDto apply(IndividualOwner owner) {
        return IndividualOwnerResponseDto.builder()
                .publicId(owner.getPublicId())
                .ownerType(enumTranslator.translate(owner.getOwnerType()))
                .status(enumTranslator.translate(owner.getStatus()))
                .email(owner.getEmail())
                .phoneNumber(owner.getPhoneNumber())
                .firstName(owner.getFirstName())
                .lastName(owner.getLastName())
                .fiscalCode(owner.getFiscalCode())
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

