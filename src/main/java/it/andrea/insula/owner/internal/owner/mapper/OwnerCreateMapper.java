package it.andrea.insula.owner.internal.owner.mapper;

import it.andrea.insula.owner.internal.owner.dto.request.OwnerCreateDto;
import it.andrea.insula.owner.internal.owner.model.BankAccount;
import it.andrea.insula.owner.internal.owner.model.Owner;
import it.andrea.insula.owner.internal.owner.model.OwnerAddress;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class OwnerCreateMapper implements Function<OwnerCreateDto, Owner> {

    @Override
    public Owner apply(OwnerCreateDto dto) {
        Owner owner = new Owner();
        owner.setType(dto.type());
        owner.setEmail(dto.email());
        owner.setPhoneNumber(dto.phoneNumber());
        owner.setFirstName(dto.firstName());
        owner.setLastName(dto.lastName());
        owner.setCompanyName(dto.companyName());
        owner.setFiscalCode(dto.fiscalCode());
        owner.setVatNumber(dto.vatNumber());
        owner.setSdiCode(dto.sdiCode());
        owner.setPecEmail(dto.pecEmail());

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

        return owner;
    }
}

