package it.andrea.insula.customer.internal.customer.mapper;

import it.andrea.insula.customer.internal.address.mapper.CustomerAddressPatchMapper;
import it.andrea.insula.customer.internal.customer.dto.request.business.BusinessCustomerPatchDto;
import it.andrea.insula.customer.internal.customer.model.BusinessCustomer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class BusinessCustomerPatchMapper implements BiFunction<BusinessCustomerPatchDto, BusinessCustomer, BusinessCustomer> {

    private final CustomerAddressPatchMapper addressPatchMapper;

    @Override
    public BusinessCustomer apply(BusinessCustomerPatchDto dto, BusinessCustomer customer) {
        if (dto.email() != null) {
            customer.setEmail(dto.email());
        }
        if (dto.phoneNumber() != null) {
            customer.setPhoneNumber(dto.phoneNumber());
        }
        if (dto.companyName() != null) {
            customer.setCompanyName(dto.companyName());
        }
        if (dto.vatNumber() != null) {
            customer.setVatNumber(dto.vatNumber());
        }
        if (dto.fiscalCode() != null) {
            customer.setFiscalCode(dto.fiscalCode());
        }
        if (dto.sdiCode() != null) {
            customer.setSdiCode(dto.sdiCode());
        }
        if (dto.pecEmail() != null) {
            customer.setPecEmail(dto.pecEmail());
        }
        if (dto.legalAddress() != null && customer.getLegalAddress() != null) {
            addressPatchMapper.apply(dto.legalAddress(), customer.getLegalAddress());
        }
        if (dto.billingAddress() != null && customer.getBillingAddress() != null) {
            addressPatchMapper.apply(dto.billingAddress(), customer.getBillingAddress());
        }
        return customer;
    }
}

