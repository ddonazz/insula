package it.andrea.insula.agency.internal.mapper;

import it.andrea.insula.agency.internal.dto.request.AgencyCreateDto;
import it.andrea.insula.agency.internal.model.Agency;
import it.andrea.insula.agency.internal.model.AgencyStatus;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class AgencyCreateDtoToAgencyMapperTest {

    private final AgencyCreateDtoToAgencyMapper mapper = new AgencyCreateDtoToAgencyMapper();

    @Test
    void apply_shouldMapAllFields() {
        AgencyCreateDto dto = new AgencyCreateDto(
                "My Agency", "12345678901", "ABCDEF12G34H567I",
                "ABC1234", "pec@agency.com", "info@agency.com",
                "+39123456789", "https://agency.com", "https://agency.com/logo.png",
                "Europe/London"
        );

        Agency agency = mapper.apply(dto);

        assertThat(agency.getName()).isEqualTo("My Agency");
        assertThat(agency.getVatNumber()).isEqualTo("12345678901");
        assertThat(agency.getFiscalCode()).isEqualTo("ABCDEF12G34H567I");
        assertThat(agency.getSdiCode()).isEqualTo("ABC1234");
        assertThat(agency.getPecEmail()).isEqualTo("pec@agency.com");
        assertThat(agency.getContactEmail()).isEqualTo("info@agency.com");
        assertThat(agency.getPhoneNumber()).isEqualTo("+39123456789");
        assertThat(agency.getWebsiteUrl()).isEqualTo("https://agency.com");
        assertThat(agency.getLogoUrl()).isEqualTo("https://agency.com/logo.png");
        assertThat(agency.getTimeZone()).isEqualTo(ZoneId.of("Europe/London"));
        assertThat(agency.getStatus()).isEqualTo(AgencyStatus.ACTIVE);
    }

    @Test
    void apply_shouldSetDefaultTimeZone_whenNull() {
        AgencyCreateDto dto = new AgencyCreateDto(
                "My Agency", "12345678901", null, null,
                null, "info@agency.com", null, null, null, null
        );

        Agency agency = mapper.apply(dto);

        assertThat(agency.getTimeZone()).isEqualTo(ZoneId.of("Europe/Rome"));
    }
}

