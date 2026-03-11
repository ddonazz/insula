package it.andrea.insula.owner.internal.owner.mapper;

import it.andrea.insula.owner.internal.owner.dto.request.BankAccountDto;
import it.andrea.insula.owner.internal.owner.dto.request.OwnerAddressDto;
import it.andrea.insula.owner.internal.owner.dto.request.OwnerCreateDto;
import it.andrea.insula.owner.internal.owner.model.Owner;
import it.andrea.insula.owner.internal.owner.model.OwnerStatus;
import it.andrea.insula.owner.internal.owner.model.OwnerType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerCreateMapperTest {

    private final OwnerCreateMapper mapper = new OwnerCreateMapper();

    @Test
    void apply_shouldMapAllFieldsForIndividual() {
        OwnerAddressDto address = new OwnerAddressDto("Via Roma", "1", "00100", "Roma", "RM", "IT");
        BankAccountDto bank = new BankAccountDto("IT60X0542811101000000123456", "UNCRITM1", null, null, "UniCredit", "IT", "Mario Rossi");

        OwnerCreateDto dto = new OwnerCreateDto(
                OwnerType.INDIVIDUAL, "mario@rossi.it", "+39123456789",
                "Mario", "Rossi", null, "RSSMRA80A01H501Z",
                null, null, null, address, bank
        );

        Owner result = mapper.apply(dto);

        assertThat(result.getType()).isEqualTo(OwnerType.INDIVIDUAL);
        assertThat(result.getEmail()).isEqualTo("mario@rossi.it");
        assertThat(result.getPhoneNumber()).isEqualTo("+39123456789");
        assertThat(result.getFirstName()).isEqualTo("Mario");
        assertThat(result.getLastName()).isEqualTo("Rossi");
        assertThat(result.getFiscalCode()).isEqualTo("RSSMRA80A01H501Z");
        assertThat(result.getStatus()).isEqualTo(OwnerStatus.ACTIVE);
        assertThat(result.getAddress()).isNotNull();
        assertThat(result.getAddress().getStreet()).isEqualTo("Via Roma");
        assertThat(result.getAddress().getCity()).isEqualTo("Roma");
        assertThat(result.getBankAccount()).isNotNull();
        assertThat(result.getBankAccount().getIban()).isEqualTo("IT60X0542811101000000123456");
        assertThat(result.getBankAccount().getHolderName()).isEqualTo("Mario Rossi");
    }

    @Test
    void apply_shouldMapCompanyFields() {
        OwnerCreateDto dto = new OwnerCreateDto(
                OwnerType.COMPANY, "info@acme.it", null,
                null, null, "ACME Srl", "12345678901",
                "12345678901", "ABC1234", "pec@acme.it", null, null
        );

        Owner result = mapper.apply(dto);

        assertThat(result.getType()).isEqualTo(OwnerType.COMPANY);
        assertThat(result.getCompanyName()).isEqualTo("ACME Srl");
        assertThat(result.getVatNumber()).isEqualTo("12345678901");
        assertThat(result.getSdiCode()).isEqualTo("ABC1234");
        assertThat(result.getPecEmail()).isEqualTo("pec@acme.it");
    }

    @Test
    void apply_shouldHandleNullAddressAndBankAccount() {
        OwnerCreateDto dto = new OwnerCreateDto(
                OwnerType.INDIVIDUAL, "mario@rossi.it", null,
                "Mario", "Rossi", null, "RSSMRA80A01H501Z",
                null, null, null, null, null
        );

        Owner result = mapper.apply(dto);

        assertThat(result.getAddress()).isNull();
        assertThat(result.getBankAccount()).isNull();
    }
}

