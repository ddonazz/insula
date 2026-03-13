package it.andrea.insula.owner.internal.owner.mapper;

import it.andrea.insula.owner.internal.owner.dto.request.IndividualOwnerCreateDto;
import it.andrea.insula.owner.internal.owner.model.IndividualOwner;
import it.andrea.insula.owner.internal.owner.model.OwnerAddress;
import it.andrea.insula.owner.internal.owner.model.BankAccount;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class IndividualOwnerCreateMapper implements Function<IndividualOwnerCreateDto, IndividualOwner> {

    @Override
    public IndividualOwner apply(IndividualOwnerCreateDto dto) {
        IndividualOwner owner = new IndividualOwner();
        owner.setEmail(dto.email());
        owner.setPhoneNumber(dto.phoneNumber());
        owner.setFirstName(dto.firstName());
        owner.setLastName(dto.lastName());
        owner.setFiscalCode(dto.fiscalCode());
        mapAddress(dto, owner);
        mapBankAccount(dto, owner);
        return owner;
    }

    private void mapAddress(IndividualOwnerCreateDto dto, IndividualOwner owner) {
        if (dto.address() != null) {
            OwnerAddress address = new OwnerAddress();
            address.setStreet(dto.address().street());
            address.setStreetNumber(dto.address().streetNumber());
            address.setZipCode(dto.address().zipCode());
            address.setCity(dto.address().city());
            address.setProvince(dto.address().province());
            address.setCountry(dto.address().country());
            owner.setAddress(address);
        }
    }

    private void mapBankAccount(IndividualOwnerCreateDto dto, IndividualOwner owner) {
        if (dto.bankAccount() != null) {
            BankAccount bank = new BankAccount();
            bank.setIban(dto.bankAccount().iban());
            bank.setSwiftBic(dto.bankAccount().swiftBic());
            bank.setExtraEuAccountNumber(dto.bankAccount().extraEuAccountNumber());
            bank.setRoutingCode(dto.bankAccount().routingCode());
            bank.setBankName(dto.bankAccount().bankName());
            bank.setBankCountryCode(dto.bankAccount().bankCountryCode());
            bank.setHolderName(dto.bankAccount().holderName());
            owner.setBankAccount(bank);
        }
    }
}

