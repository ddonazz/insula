package it.andrea.insula.owner.internal.owner.mapper;

import it.andrea.insula.core.dto.EnumTranslator;
import it.andrea.insula.core.dto.TranslatedEnum;
import it.andrea.insula.owner.internal.owner.dto.response.BusinessOwnerResponseDto;
import it.andrea.insula.owner.internal.owner.model.BusinessOwner;
import it.andrea.insula.owner.internal.owner.model.OwnerStatus;
import it.andrea.insula.owner.internal.owner.model.OwnerType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BusinessOwnerResponseMapperTest {

    private final EnumTranslator enumTranslator = mock(EnumTranslator.class);
    private final BusinessOwnerResponseMapper mapper = new BusinessOwnerResponseMapper(enumTranslator);

    @Test
    void apply_shouldMapDisplayName() {
        BusinessOwner owner = new BusinessOwner();
        owner.setPublicId(UUID.randomUUID());
        owner.setOwnerType(OwnerType.BUSINESS);
        owner.setStatus(OwnerStatus.ACTIVE);
        owner.setEmail("info@acme.it");
        owner.setPhoneNumber("+390000000");
        owner.setDisplayName("ACME SRL");
        owner.setCompanyName("ACME SRL");
        owner.setFiscalCode("ACMEFISCAL001");
        owner.setVatNumber("12345678901");

        when(enumTranslator.translate(OwnerType.BUSINESS)).thenReturn(new TranslatedEnum("BUSINESS", "Azienda"));
        when(enumTranslator.translate(OwnerStatus.ACTIVE)).thenReturn(new TranslatedEnum("ACTIVE", "Attivo"));

        BusinessOwnerResponseDto result = mapper.apply(owner);

        assertThat(result.displayName()).isEqualTo("ACME SRL");
        assertThat(result.companyName()).isEqualTo("ACME SRL");
        assertThat(result.vatNumber()).isEqualTo("12345678901");
    }
}

