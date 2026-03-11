package it.andrea.insula.owner.internal.owner.mapper;

import it.andrea.insula.owner.internal.owner.dto.request.OwnerPatchDto;
import it.andrea.insula.owner.internal.owner.model.BankAccount;
import it.andrea.insula.owner.internal.owner.model.Owner;
import it.andrea.insula.owner.internal.owner.model.OwnerAddress;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class OwnerPatchMapper implements BiFunction<OwnerPatchDto, Owner, Owner> {

    @Override
    public Owner apply(OwnerPatchDto dto, Owner owner) {
        if (dto.type() != null) {
            owner.setType(dto.type());
        }
        if (dto.email() != null) {
            owner.setEmail(dto.email());
        }
        if (dto.phoneNumber() != null) {
            owner.setPhoneNumber(dto.phoneNumber());
        }
        if (dto.firstName() != null) {
            owner.setFirstName(dto.firstName());
        }
        if (dto.lastName() != null) {
            owner.setLastName(dto.lastName());
        }
        if (dto.companyName() != null) {
            owner.setCompanyName(dto.companyName());
        }
        if (dto.fiscalCode() != null) {
            owner.setFiscalCode(dto.fiscalCode());
        }
        if (dto.vatNumber() != null) {
            owner.setVatNumber(dto.vatNumber());
        }
        if (dto.sdiCode() != null) {
            owner.setSdiCode(dto.sdiCode());
        }
        if (dto.pecEmail() != null) {
            owner.setPecEmail(dto.pecEmail());
        }

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

