package it.andrea.insula.agency.internal.mapper;

import it.andrea.insula.agency.internal.dto.response.AgencyResponseDto;
import it.andrea.insula.agency.internal.model.Agency;
import it.andrea.insula.core.dto.EnumTranslator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class AgencyToAgencyResponseDtoMapper implements Function<Agency, AgencyResponseDto> {

    private final EnumTranslator enumTranslator;

    @Override
    public AgencyResponseDto apply(Agency agency) {
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
                .status(enumTranslator.translate(agency.getStatus()))
                .build();
    }
}
