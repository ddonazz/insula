package it.andrea.insula.customer.internal.customer.mapper;

import it.andrea.insula.customer.internal.address.dto.request.CustomerAddressCreateDto;
import it.andrea.insula.customer.internal.address.mapper.CustomerAddressCreateMapper;
import it.andrea.insula.customer.internal.customer.dto.request.business.BusinessCustomerCreateDto;
import it.andrea.insula.customer.internal.customer.model.BusinessCustomer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessCustomerCreateMapperTest {

    private final CustomerAddressCreateMapper addressMapper = new CustomerAddressCreateMapper();
    private final BusinessCustomerCreateMapper mapper = new BusinessCustomerCreateMapper(addressMapper);

    @Test
    void apply_shouldMapAllFields() {
        CustomerAddressCreateDto address = new CustomerAddressCreateDto("Via Roma", "1", "00100", "Roma", "RM", "IT");
        BusinessCustomerCreateDto dto = new BusinessCustomerCreateDto(
                "info@acme.it", "+39123456", "ACME Srl", "12345678901",
                "ACMEFISCAL123456", address, address, "ABC1234", "pec@acme.it"
        );

        BusinessCustomer result = mapper.apply(dto);

        assertThat(result.getEmail()).isEqualTo("info@acme.it");
        assertThat(result.getPhoneNumber()).isEqualTo("+39123456");
        assertThat(result.getCompanyName()).isEqualTo("ACME Srl");
        assertThat(result.getVatNumber()).isEqualTo("12345678901");
        assertThat(result.getFiscalCode()).isEqualTo("ACMEFISCAL123456");
        assertThat(result.getSdiCode()).isEqualTo("ABC1234");
        assertThat(result.getPecEmail()).isEqualTo("pec@acme.it");
        assertThat(result.getLegalAddress()).isNotNull();
        assertThat(result.getBillingAddress()).isNotNull();
    }
}

