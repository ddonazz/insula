package it.andrea.insula.agency.internal.mapper;

import it.andrea.insula.agency.internal.dto.response.AgencyResponseDto;
import it.andrea.insula.agency.internal.model.Agency;
import it.andrea.insula.agency.internal.model.AgencyStatus;
import it.andrea.insula.core.dto.TranslatedEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class AgencyToAgencyResponseDtoMapper implements Function<Agency, AgencyResponseDto> {

    private final MessageSource messageSource;

    @Override
    public AgencyResponseDto apply(Agency agency) {
        Locale locale = LocaleContextHolder.getLocale();

        return AgencyResponseDto.builder()
                .id(agency.getId())
                .publicId(agency.getPublicId())
                .name(agency.getName())
                .vatNumber(agency.getVatNumber())
                .fiscalCode(agency.getFiscalCode())
                .sdiCode(agency.getSdiCode())
                .pecEmail(agency.getPecEmail())
                .contactEmail(agency.getContactEmail())
                .phoneNumber(agency.getPhoneNumber())
                .websiteUrl(agency.getWebsiteUrl())
                .logoUrl(agency.getLogoUrl())
                .timeZone(agency.getTimeZone() != null ? agency.getTimeZone().getId() : null)
                .status(translateStatus(agency.getStatus(), locale))
                .build();
    }

    private TranslatedEnum translateStatus(AgencyStatus status, Locale locale) {
        if (status == null) {
            return null;
        }
        String code = "enum.agencystatus." + status.name();
        String label = messageSource.getMessage(code, null, status.name(), locale);
        return new TranslatedEnum(status.name(), label);
    }
}

