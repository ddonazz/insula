package it.andrea.insula.customer.internal.customer.mapper;

import it.andrea.insula.customer.internal.address.dto.request.CustomerAddressCreateDto;
import it.andrea.insula.customer.internal.address.mapper.CustomerAddressCreateMapper;
import it.andrea.insula.customer.internal.customer.dto.request.IndividualCustomerCreateDto;
import it.andrea.insula.customer.internal.customer.model.IndividualCustomer;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class IndividualCustomerCreateMapperTest {

    private final CustomerAddressCreateMapper addressMapper = new CustomerAddressCreateMapper();
    private final IndividualCustomerCreateMapper mapper = new IndividualCustomerCreateMapper(addressMapper);

    @Test
    void apply_shouldMapAllFields() {
        CustomerAddressCreateDto address = new CustomerAddressCreateDto("Via Roma", "1", "00100", "Roma", "RM", "IT");
        IndividualCustomerCreateDto dto = new IndividualCustomerCreateDto(
                "mario@rossi.it", "+39123456", "Mario", "Rossi",
                "RSSMRA80A01H501Z", LocalDate.of(1980, 1, 1), "Roma", "Italiana", address
        );

        IndividualCustomer result = mapper.apply(dto);

        assertThat(result.getEmail()).isEqualTo("mario@rossi.it");
        assertThat(result.getPhoneNumber()).isEqualTo("+39123456");
        assertThat(result.getFirstName()).isEqualTo("Mario");
        assertThat(result.getLastName()).isEqualTo("Rossi");
        assertThat(result.getFiscalCode()).isEqualTo("RSSMRA80A01H501Z");
        assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(1980, 1, 1));
        assertThat(result.getBirthPlace()).isEqualTo("Roma");
        assertThat(result.getNationality()).isEqualTo("Italiana");
        assertThat(result.getBillingAddress()).isNotNull();
        assertThat(result.getBillingAddress().getStreet()).isEqualTo("Via Roma");
    }
}

