package it.andrea.insula.owner.internal.owner.mapper;

import it.andrea.insula.core.dto.EnumTranslator;
import it.andrea.insula.core.dto.TranslatedEnum;
import it.andrea.insula.owner.internal.owner.dto.response.IndividualOwnerResponseDto;
import it.andrea.insula.owner.internal.owner.model.IndividualOwner;
import it.andrea.insula.owner.internal.owner.model.OwnerStatus;
import it.andrea.insula.owner.internal.owner.model.OwnerType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IndividualOwnerResponseMapperTest {

    private final EnumTranslator enumTranslator = mock(EnumTranslator.class);
    private final IndividualOwnerResponseMapper mapper = new IndividualOwnerResponseMapper(enumTranslator);

    @Test
    void apply_shouldMapDisplayName() {
        IndividualOwner owner = new IndividualOwner();
        owner.setPublicId(UUID.randomUUID());
        owner.setOwnerType(OwnerType.INDIVIDUAL);
        owner.setStatus(OwnerStatus.ACTIVE);
        owner.setEmail("mario.rossi@test.it");
        owner.setPhoneNumber("+390000000");
        owner.setDisplayName("Mario Rossi");
        owner.setFirstName("Mario");
        owner.setLastName("Rossi");
        owner.setFiscalCode("RSSMRA80A01H501Z");

        when(enumTranslator.translate(OwnerType.INDIVIDUAL)).thenReturn(new TranslatedEnum("INDIVIDUAL", "Privato"));
        when(enumTranslator.translate(OwnerStatus.ACTIVE)).thenReturn(new TranslatedEnum("ACTIVE", "Attivo"));

        IndividualOwnerResponseDto result = mapper.apply(owner);

        assertThat(result.displayName()).isEqualTo("Mario Rossi");
        assertThat(result.firstName()).isEqualTo("Mario");
        assertThat(result.lastName()).isEqualTo("Rossi");
    }
}

