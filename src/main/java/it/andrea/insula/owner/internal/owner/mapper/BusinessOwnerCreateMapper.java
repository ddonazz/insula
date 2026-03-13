package it.andrea.insula.owner.internal.owner.mapper;

import it.andrea.insula.owner.internal.owner.dto.request.BusinessOwnerCreateDto;
import it.andrea.insula.owner.internal.owner.model.BankAccount;
import it.andrea.insula.owner.internal.owner.model.BusinessOwner;
import it.andrea.insula.owner.internal.owner.model.OwnerAddress;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class BusinessOwnerCreateMapper implements Function<BusinessOwnerCreateDto, BusinessOwner> {

    @Override
    public BusinessOwner apply(BusinessOwnerCreateDto dto) {
        BusinessOwner owner = new BusinessOwner();
        owner.setEmail(dto.email());
        owner.setPhoneNumber(dto.phoneNumber());
        owner.setCompanyName(dto.companyName());
        owner.setFiscalCode(dto.fiscalCode());
        owner.setVatNumber(dto.vatNumber());
        owner.setSdiCode(dto.sdiCode());
        owner.setPecEmail(dto.pecEmail());
        mapAddress(dto, owner);
        mapBankAccount(dto, owner);
        return owner;
    }

    private void mapAddress(BusinessOwnerCreateDto dto, BusinessOwner owner) {
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

    private void mapBankAccount(BusinessOwnerCreateDto dto, BusinessOwner owner) {
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

