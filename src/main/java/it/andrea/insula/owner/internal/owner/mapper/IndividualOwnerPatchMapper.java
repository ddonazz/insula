package it.andrea.insula.owner.internal.owner.mapper;

import it.andrea.insula.owner.internal.owner.dto.request.IndividualOwnerPatchDto;
import it.andrea.insula.owner.internal.owner.model.BankAccount;
import it.andrea.insula.owner.internal.owner.model.IndividualOwner;
import it.andrea.insula.owner.internal.owner.model.OwnerAddress;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class IndividualOwnerPatchMapper implements BiFunction<IndividualOwnerPatchDto, IndividualOwner, IndividualOwner> {

    @Override
    public IndividualOwner apply(IndividualOwnerPatchDto dto, IndividualOwner owner) {
        if (dto.email() != null) owner.setEmail(dto.email());
        if (dto.phoneNumber() != null) owner.setPhoneNumber(dto.phoneNumber());
        if (dto.firstName() != null) owner.setFirstName(dto.firstName());
        if (dto.lastName() != null) owner.setLastName(dto.lastName());
        if (dto.fiscalCode() != null) owner.setFiscalCode(dto.fiscalCode());

        if (dto.address() != null) {
            OwnerAddress address = owner.getAddress() != null ? owner.getAddress() : new OwnerAddress();
            if (dto.address().street() != null) address.setStreet(dto.address().street());
            if (dto.address().streetNumber() != null) address.setStreetNumber(dto.address().streetNumber());
            if (dto.address().zipCode() != null) address.setZipCode(dto.address().zipCode());
            if (dto.address().city() != null) address.setCity(dto.address().city());
            if (dto.address().province() != null) address.setProvince(dto.address().province());
            if (dto.address().country() != null) address.setCountry(dto.address().country());
            owner.setAddress(address);
        }

        if (dto.bankAccount() != null) {
            BankAccount bank = owner.getBankAccount() != null ? owner.getBankAccount() : new BankAccount();
            if (dto.bankAccount().iban() != null) bank.setIban(dto.bankAccount().iban());
            if (dto.bankAccount().swiftBic() != null) bank.setSwiftBic(dto.bankAccount().swiftBic());
            if (dto.bankAccount().extraEuAccountNumber() != null) bank.setExtraEuAccountNumber(dto.bankAccount().extraEuAccountNumber());
            if (dto.bankAccount().routingCode() != null) bank.setRoutingCode(dto.bankAccount().routingCode());
            if (dto.bankAccount().bankName() != null) bank.setBankName(dto.bankAccount().bankName());
            if (dto.bankAccount().bankCountryCode() != null) bank.setBankCountryCode(dto.bankAccount().bankCountryCode());
            if (dto.bankAccount().holderName() != null) bank.setHolderName(dto.bankAccount().holderName());
            owner.setBankAccount(bank);
        }

        return owner;
    }
}

