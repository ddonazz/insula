package it.andrea.insula.customer.internal.customer.mapper;

import it.andrea.insula.customer.internal.address.mapper.CustomerAddressCreateMapper;
import it.andrea.insula.customer.internal.customer.dto.request.business.BusinessCustomerCreateDto;
import it.andrea.insula.customer.internal.customer.model.BusinessCustomer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class BusinessCustomerCreateMapper implements Function<BusinessCustomerCreateDto, BusinessCustomer> {

    private final CustomerAddressCreateMapper addressMapper;

    @Override
    public BusinessCustomer apply(BusinessCustomerCreateDto dto) {
        BusinessCustomer customer = new BusinessCustomer();
        customer.setEmail(dto.email());
        customer.setPhoneNumber(dto.phoneNumber());
        customer.setCompanyName(dto.companyName());
        customer.setVatNumber(dto.vatNumber());
        customer.setFiscalCode(dto.fiscalCode());
        customer.setSdiCode(dto.sdiCode());
        customer.setPecEmail(dto.pecEmail());
        if (dto.legalAddress() != null) {
            customer.setLegalAddress(addressMapper.apply(dto.legalAddress()));
        }
        if (dto.billingAddress() != null) {
            customer.setBillingAddress(addressMapper.apply(dto.billingAddress()));
        }
        return customer;
    }
}

