package it.andrea.insula.owner.internal.owner.mapper;

import it.andrea.insula.owner.internal.owner.dto.request.IndividualOwnerUpdateDto;
import it.andrea.insula.owner.internal.owner.model.BankAccount;
import it.andrea.insula.owner.internal.owner.model.IndividualOwner;
import it.andrea.insula.owner.internal.owner.model.OwnerAddress;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class IndividualOwnerUpdateMapper implements BiFunction<IndividualOwnerUpdateDto, IndividualOwner, IndividualOwner> {

    @Override
    public IndividualOwner apply(IndividualOwnerUpdateDto dto, IndividualOwner owner) {
        owner.setEmail(dto.email());
        owner.setPhoneNumber(dto.phoneNumber());
        owner.setFirstName(dto.firstName());
        owner.setLastName(dto.lastName());
        owner.setFiscalCode(dto.fiscalCode());

        if (dto.address() != null) {
            OwnerAddress address = owner.getAddress() != null ? owner.getAddress() : new OwnerAddress();
            address.setStreet(dto.address().street());
            address.setStreetNumber(dto.address().streetNumber());
            address.setZipCode(dto.address().zipCode());
            address.setCity(dto.address().city());
            address.setProvince(dto.address().province());
            address.setCountry(dto.address().country());
            owner.setAddress(address);
        } else {
            owner.setAddress(null);
        }

        if (dto.bankAccount() != null) {
            BankAccount bank = owner.getBankAccount() != null ? owner.getBankAccount() : new BankAccount();
            bank.setIban(dto.bankAccount().iban());
            bank.setSwiftBic(dto.bankAccount().swiftBic());
            bank.setExtraEuAccountNumber(dto.bankAccount().extraEuAccountNumber());
            bank.setRoutingCode(dto.bankAccount().routingCode());
            bank.setBankName(dto.bankAccount().bankName());
            bank.setBankCountryCode(dto.bankAccount().bankCountryCode());
            bank.setHolderName(dto.bankAccount().holderName());
            owner.setBankAccount(bank);
        } else {
            owner.setBankAccount(null);
        }

        return owner;
    }
}

