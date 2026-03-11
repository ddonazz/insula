package it.andrea.insula.owner.internal.owner.mapper;

import it.andrea.insula.owner.internal.owner.dto.request.BankAccountDto;
import it.andrea.insula.owner.internal.owner.dto.request.OwnerAddressDto;
import it.andrea.insula.owner.internal.owner.dto.request.OwnerPatchDto;
import it.andrea.insula.owner.internal.owner.model.BankAccount;
import it.andrea.insula.owner.internal.owner.model.Owner;
import it.andrea.insula.owner.internal.owner.model.OwnerAddress;
import it.andrea.insula.owner.internal.owner.model.OwnerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OwnerPatchMapperTest {

    private final OwnerPatchMapper mapper = new OwnerPatchMapper();
    private Owner owner;

    @BeforeEach
    void setUp() {
        OwnerAddress address = new OwnerAddress();
        address.setStreet("Via Vecchia");
        address.setCity("Milano");
        address.setCountry("IT");

        BankAccount bank = new BankAccount();
        bank.setIban("IT60X0542811101000000000001");
        bank.setHolderName("Mario Rossi");

        owner = new Owner();
        owner.setType(OwnerType.INDIVIDUAL);
        owner.setEmail("old@email.it");
        owner.setFirstName("Mario");
        owner.setLastName("Rossi");
        owner.setFiscalCode("RSSMRA80A01H501Z");
        owner.setAddress(address);
        owner.setBankAccount(bank);
    }

    @Test
    void apply_shouldUpdateAllFieldsWhenProvided() {
        OwnerAddressDto newAddress = new OwnerAddressDto("Via Nuova", "5", "20100", "Roma", "RM", "IT");
        OwnerPatchDto dto = new OwnerPatchDto(
                OwnerType.COMPANY, "new@email.it", "+39987654321",
                "Luigi", "Verdi", "ACME Srl", "VRDLGU70B01F205X",
                "98765432101", null, null, newAddress, null
        );

        Owner result = mapper.apply(dto, owner);

        assertThat(result.getType()).isEqualTo(OwnerType.COMPANY);
        assertThat(result.getEmail()).isEqualTo("new@email.it");
        assertThat(result.getFirstName()).isEqualTo("Luigi");
        assertThat(result.getLastName()).isEqualTo("Verdi");
        assertThat(result.getCompanyName()).isEqualTo("ACME Srl");
        assertThat(result.getFiscalCode()).isEqualTo("VRDLGU70B01F205X");
        assertThat(result.getAddress().getStreet()).isEqualTo("Via Nuova");
        assertThat(result.getAddress().getCity()).isEqualTo("Roma");
    }

    @Test
    void apply_shouldSkipNullFields() {
        OwnerPatchDto dto = new OwnerPatchDto(
                null, null, null, null, null, null, null,
                null, null, null, null, null
        );

        Owner result = mapper.apply(dto, owner);

        assertThat(result.getEmail()).isEqualTo("old@email.it");
        assertThat(result.getFirstName()).isEqualTo("Mario");
        assertThat(result.getLastName()).isEqualTo("Rossi");
        assertThat(result.getAddress().getStreet()).isEqualTo("Via Vecchia");
        assertThat(result.getBankAccount().getIban()).isEqualTo("IT60X0542811101000000000001");
    }

    @Test
    void apply_shouldUpdateOnlyEmail() {
        OwnerPatchDto dto = new OwnerPatchDto(
                null, "updated@email.it", null, null, null, null, null,
                null, null, null, null, null
        );

        Owner result = mapper.apply(dto, owner);

        assertThat(result.getEmail()).isEqualTo("updated@email.it");
        assertThat(result.getFirstName()).isEqualTo("Mario");
    }

    @Test
    void apply_shouldUpdateAddressFieldsPartially() {
        OwnerAddressDto partialAddress = new OwnerAddressDto("Via Aggiornata", null, null, null, null, null);
        OwnerPatchDto dto = new OwnerPatchDto(
                null, null, null, null, null, null, null,
                null, null, null, partialAddress, null
        );

        Owner result = mapper.apply(dto, owner);

        assertThat(result.getAddress().getStreet()).isEqualTo("Via Aggiornata");
        assertThat(result.getAddress().getCity()).isEqualTo("Milano"); // preserved
    }

    @Test
    void apply_shouldUpdateBankAccountFieldsPartially() {
        BankAccountDto partialBank = new BankAccountDto("IT60X9999999999999999999999", null, null, null, null, null, null);
        OwnerPatchDto dto = new OwnerPatchDto(
                null, null, null, null, null, null, null,
                null, null, null, null, partialBank
        );

        Owner result = mapper.apply(dto, owner);

        assertThat(result.getBankAccount().getIban()).isEqualTo("IT60X9999999999999999999999");
        assertThat(result.getBankAccount().getHolderName()).isEqualTo("Mario Rossi"); // preserved
    }

    @Test
    void apply_shouldReturnSameOwnerInstance() {
        OwnerPatchDto dto = new OwnerPatchDto(
                null, null, null, null, null, null, null,
                null, null, null, null, null
        );

        Owner result = mapper.apply(dto, owner);

        assertThat(result).isSameAs(owner);
    }
}

