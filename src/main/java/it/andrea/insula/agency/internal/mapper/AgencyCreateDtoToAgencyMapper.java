package it.andrea.insula.agency.internal.mapper;

import it.andrea.insula.agency.internal.dto.request.AgencyCreateDto;
import it.andrea.insula.agency.internal.model.Agency;
import it.andrea.insula.agency.internal.model.AgencyStatus;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.function.Function;

@Component
public class AgencyCreateDtoToAgencyMapper implements Function<AgencyCreateDto, Agency> {

    private static final ZoneId DEFAULT_ZONE = ZoneId.of("Europe/Rome");

    @Override
    public Agency apply(@NonNull AgencyCreateDto dto) {
        Agency agency = new Agency();
        agency.setName(dto.name());
        agency.setVatNumber(dto.vatNumber());
        agency.setFiscalCode(dto.fiscalCode());
        agency.setSdiCode(dto.sdiCode());
        agency.setPecEmail(dto.pecEmail());
        agency.setContactEmail(dto.contactEmail());
        agency.setPhoneNumber(dto.phoneNumber());
        agency.setWebsiteUrl(dto.websiteUrl());
        agency.setLogoUrl(dto.logoUrl());
        agency.setTimeZone(dto.timeZone() != null ? ZoneId.of(dto.timeZone()) : DEFAULT_ZONE);
        agency.setStatus(AgencyStatus.ACTIVE);
        return agency;
    }
}

