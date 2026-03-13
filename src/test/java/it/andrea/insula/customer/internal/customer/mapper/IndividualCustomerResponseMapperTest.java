package it.andrea.insula.customer.internal.customer.mapper;

import it.andrea.insula.customer.internal.address.mapper.CustomerAddressResponseMapper;
import it.andrea.insula.customer.internal.address.model.CustomerAddress;
import it.andrea.insula.customer.internal.customer.dto.response.IndividualCustomerResponseDto;
import it.andrea.insula.customer.internal.customer.model.CustomerType;
import it.andrea.insula.customer.internal.customer.model.IndividualCustomer;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class IndividualCustomerResponseMapperTest {

    private final CustomerAddressResponseMapper addressMapper = new CustomerAddressResponseMapper();
    private final IndividualCustomerResponseMapper mapper = new IndividualCustomerResponseMapper(addressMapper);

    @Test
    void apply_shouldMapDisplayName() {
        CustomerAddress billingAddress = CustomerAddress.builder()
                .street("Via Roma")
                .number("1")
                .postalCode("00100")
                .city("Roma")
                .country("IT")
                .build();

        IndividualCustomer customer = new IndividualCustomer();
        customer.setPublicId(UUID.randomUUID());
        customer.setCustomerType(CustomerType.INDIVIDUAL);
        customer.setEmail("mario.rossi@test.it");
        customer.setPhoneNumber("+390000000");
        customer.setDisplayName("Mario Rossi");
        customer.setFirstName("Mario");
        customer.setLastName("Rossi");
        customer.setFiscalCode("RSSMRA80A01H501Z");
        customer.setBillingAddress(billingAddress);

        IndividualCustomerResponseDto result = mapper.apply(customer);

        assertThat(result.displayName()).isEqualTo("Mario Rossi");
        assertThat(result.firstName()).isEqualTo("Mario");
        assertThat(result.lastName()).isEqualTo("Rossi");
        assertThat(result.billingAddress()).isNotNull();
    }
}

