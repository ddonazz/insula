package it.andrea.insula.agency.internal.mapper;

import it.andrea.insula.agency.internal.dto.response.AgencyResponseDto;
import it.andrea.insula.agency.internal.model.Agency;
import it.andrea.insula.agency.internal.model.AgencyStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;

import java.time.ZoneId;
import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgencyToAgencyResponseDtoMapperTest {

    @Mock
    private MessageSource messageSource;

    private AgencyToAgencyResponseDtoMapper mapper;

    private Agency agency;

    @BeforeEach
    void setUp() {
        mapper = new AgencyToAgencyResponseDtoMapper(messageSource);

        agency = new Agency();
        agency.setId(1L);
        agency.setPublicId(UUID.randomUUID());
        agency.setName("Test Agency");
        agency.setVatNumber("12345678901");
        agency.setFiscalCode("ABCDEF12G34H567I");
        agency.setSdiCode("ABC1234");
        agency.setPecEmail("pec@agency.com");
        agency.setContactEmail("info@agency.com");
        agency.setPhoneNumber("+39123456789");
        agency.setWebsiteUrl("https://agency.com");
        agency.setLogoUrl("https://agency.com/logo.png");
        agency.setTimeZone(ZoneId.of("Europe/Rome"));
        agency.setStatus(AgencyStatus.ACTIVE);
    }

    @Test
    void apply_shouldMapAllFields() {
        when(messageSource.getMessage(eq("enum.agencystatus.ACTIVE"), isNull(), anyString(), any(Locale.class)))
                .thenReturn("Attiva");

        AgencyResponseDto result = mapper.apply(agency);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.publicId()).isEqualTo(agency.getPublicId());
        assertThat(result.name()).isEqualTo("Test Agency");
        assertThat(result.vatNumber()).isEqualTo("12345678901");
        assertThat(result.fiscalCode()).isEqualTo("ABCDEF12G34H567I");
        assertThat(result.sdiCode()).isEqualTo("ABC1234");
        assertThat(result.pecEmail()).isEqualTo("pec@agency.com");
        assertThat(result.contactEmail()).isEqualTo("info@agency.com");
        assertThat(result.phoneNumber()).isEqualTo("+39123456789");
        assertThat(result.websiteUrl()).isEqualTo("https://agency.com");
        assertThat(result.logoUrl()).isEqualTo("https://agency.com/logo.png");
        assertThat(result.timeZone()).isEqualTo("Europe/Rome");
        assertThat(result.status().code()).isEqualTo("ACTIVE");
        assertThat(result.status().label()).isEqualTo("Attiva");
    }

    @Test
    void apply_shouldHandleNullStatus() {
        agency.setStatus(null);

        AgencyResponseDto result = mapper.apply(agency);

        assertThat(result.status()).isNull();
    }
}

