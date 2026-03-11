package it.andrea.insula.owner.internal.owner.mapper;

import it.andrea.insula.core.dto.EnumTranslator;
import it.andrea.insula.core.dto.TranslatedEnum;
import it.andrea.insula.owner.internal.owner.dto.response.OwnerResponseDto;
import it.andrea.insula.owner.internal.owner.model.BankAccount;
import it.andrea.insula.owner.internal.owner.model.Owner;
import it.andrea.insula.owner.internal.owner.model.OwnerAddress;
import it.andrea.insula.owner.internal.owner.model.OwnerStatus;
import it.andrea.insula.owner.internal.owner.model.OwnerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OwnerResponseMapperTest {

    @Mock
    private EnumTranslator enumTranslator;

    private OwnerResponseMapper mapper;
    private Owner owner;

    @BeforeEach
    void setUp() {
        mapper = new OwnerResponseMapper(enumTranslator);

        OwnerAddress address = new OwnerAddress();
        address.setStreet("Via Roma");
        address.setStreetNumber("1");
        address.setZipCode("00100");
        address.setCity("Roma");
        address.setProvince("RM");
        address.setCountry("IT");

        BankAccount bank = new BankAccount();
        bank.setIban("IT60X0542811101000000123456");
        bank.setBankName("UniCredit");
        bank.setHolderName("Mario Rossi");

        owner = new Owner();
        owner.setPublicId(UUID.randomUUID());
        owner.setType(OwnerType.INDIVIDUAL);
        owner.setStatus(OwnerStatus.ACTIVE);
        owner.setEmail("mario@rossi.it");
        owner.setPhoneNumber("+39123456789");
        owner.setFirstName("Mario");
        owner.setLastName("Rossi");
        owner.setFiscalCode("RSSMRA80A01H501Z");
        owner.setAddress(address);
        owner.setBankAccount(bank);
    }

    @Test
    void apply_shouldMapAllFields() {
        when(enumTranslator.translate(OwnerType.INDIVIDUAL)).thenReturn(new TranslatedEnum("INDIVIDUAL", "Privato"));
        when(enumTranslator.translate(OwnerStatus.ACTIVE)).thenReturn(new TranslatedEnum("ACTIVE", "Attivo"));

        OwnerResponseDto result = mapper.apply(owner);

        assertThat(result.publicId()).isEqualTo(owner.getPublicId());
        assertThat(result.type().code()).isEqualTo("INDIVIDUAL");
        assertThat(result.type().label()).isEqualTo("Privato");
        assertThat(result.status().code()).isEqualTo("ACTIVE");
        assertThat(result.email()).isEqualTo("mario@rossi.it");
        assertThat(result.firstName()).isEqualTo("Mario");
        assertThat(result.lastName()).isEqualTo("Rossi");
        assertThat(result.fiscalCode()).isEqualTo("RSSMRA80A01H501Z");
        assertThat(result.address()).isNotNull();
        assertThat(result.address().street()).isEqualTo("Via Roma");
        assertThat(result.address().city()).isEqualTo("Roma");
        assertThat(result.bankAccount()).isNotNull();
        assertThat(result.bankAccount().iban()).isEqualTo("IT60X0542811101000000123456");
        assertThat(result.bankAccount().holderName()).isEqualTo("Mario Rossi");
    }

    @Test
    void apply_shouldHandleNullAddressAndBankAccount() {
        owner.setAddress(null);
        owner.setBankAccount(null);
        when(enumTranslator.translate(OwnerType.INDIVIDUAL)).thenReturn(new TranslatedEnum("INDIVIDUAL", "Privato"));
        when(enumTranslator.translate(OwnerStatus.ACTIVE)).thenReturn(new TranslatedEnum("ACTIVE", "Attivo"));

        OwnerResponseDto result = mapper.apply(owner);

        assertThat(result.address()).isNull();
        assertThat(result.bankAccount()).isNull();
    }

    @Test
    void apply_shouldHandleNullEnumValues() {
        owner.setType(null);
        owner.setStatus(null);
        when(enumTranslator.translate((OwnerType) null)).thenReturn(null);
        when(enumTranslator.translate((OwnerStatus) null)).thenReturn(null);

        OwnerResponseDto result = mapper.apply(owner);

        assertThat(result.type()).isNull();
        assertThat(result.status()).isNull();
    }
}

