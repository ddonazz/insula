package it.andrea.insula.customer.internal.customer.mapper;

import it.andrea.insula.customer.internal.address.mapper.CustomerAddressResponseMapper;
import it.andrea.insula.customer.internal.address.model.CustomerAddress;
import it.andrea.insula.customer.internal.customer.dto.response.BusinessCustomerResponseDto;
import it.andrea.insula.customer.internal.customer.model.BusinessCustomer;
import it.andrea.insula.customer.internal.customer.model.CustomerContact;
import it.andrea.insula.customer.internal.customer.model.CustomerType;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessCustomerResponseMapperTest {

    private final CustomerAddressResponseMapper addressMapper = new CustomerAddressResponseMapper();
    private final BusinessCustomerResponseMapper mapper = new BusinessCustomerResponseMapper(addressMapper);

    @Test
    void apply_shouldMapDisplayName() {
        CustomerAddress legalAddress = CustomerAddress.builder()
                .street("Via Milano")
                .number("10")
                .postalCode("20100")
                .city("Milano")
                .country("IT")
                .build();

        CustomerContact contact = new CustomerContact();
        contact.setPublicId(UUID.randomUUID());
        contact.setFirstName("Giulia");
        contact.setLastName("Bianchi");
        contact.setEmail("giulia.bianchi@acme.it");

        BusinessCustomer customer = new BusinessCustomer();
        customer.setPublicId(UUID.randomUUID());
        customer.setCustomerType(CustomerType.BUSINESS);
        customer.setEmail("info@acme.it");
        customer.setPhoneNumber("+390000000");
        customer.setDisplayName("ACME SRL");
        customer.setCompanyName("ACME SRL");
        customer.setVatNumber("12345678901");
        customer.setFiscalCode("ACMEFISCAL001");
        customer.setLegalAddress(legalAddress);
        customer.setContacts(Set.of(contact));

        BusinessCustomerResponseDto result = mapper.apply(customer);

        assertThat(result.displayName()).isEqualTo("ACME SRL");
        assertThat(result.companyName()).isEqualTo("ACME SRL");
        assertThat(result.contacts()).hasSize(1);
    }
}

