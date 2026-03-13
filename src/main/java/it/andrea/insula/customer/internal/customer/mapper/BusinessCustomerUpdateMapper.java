package it.andrea.insula.customer.internal.customer.mapper;

import it.andrea.insula.customer.internal.address.mapper.CustomerAddressUpdateMapper;
import it.andrea.insula.customer.internal.customer.dto.request.BusinessCustomerUpdateDto;
import it.andrea.insula.customer.internal.customer.model.BusinessCustomer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class BusinessCustomerUpdateMapper implements BiFunction<BusinessCustomerUpdateDto, BusinessCustomer, BusinessCustomer> {

    private final CustomerAddressUpdateMapper addressUpdateMapper;

    @Override
    public BusinessCustomer apply(BusinessCustomerUpdateDto dto, BusinessCustomer customer) {
        customer.setEmail(dto.email());
        customer.setPhoneNumber(dto.phoneNumber());
        customer.setCompanyName(dto.companyName());
        customer.setVatNumber(dto.vatNumber());
        customer.setFiscalCode(dto.fiscalCode());
        customer.setSdiCode(dto.sdiCode());
        customer.setPecEmail(dto.pecEmail());
        if (dto.legalAddress() != null && customer.getLegalAddress() != null) {
            addressUpdateMapper.apply(dto.legalAddress(), customer.getLegalAddress());
        }
        if (dto.billingAddress() != null && customer.getBillingAddress() != null) {
            addressUpdateMapper.apply(dto.billingAddress(), customer.getBillingAddress());
        }
        return customer;
    }
}
