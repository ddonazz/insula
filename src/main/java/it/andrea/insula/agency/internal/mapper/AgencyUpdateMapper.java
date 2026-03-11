package it.andrea.insula.agency.internal.mapper;

import it.andrea.insula.agency.internal.dto.request.AgencyUpdateDto;
import it.andrea.insula.agency.internal.model.Agency;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.function.BiFunction;

@Component
public class AgencyUpdateMapper implements BiFunction<AgencyUpdateDto, Agency, Agency> {

    @Override
    public Agency apply(AgencyUpdateDto dto, Agency agency) {
        agency.setName(dto.name());
        agency.setVatNumber(dto.vatNumber());
        agency.setFiscalCode(dto.fiscalCode());
        agency.setSdiCode(dto.sdiCode());
        agency.setPecEmail(dto.pecEmail());
        agency.setContactEmail(dto.contactEmail());
        agency.setPhoneNumber(dto.phoneNumber());
        agency.setWebsiteUrl(dto.websiteUrl());
        agency.setLogoUrl(dto.logoUrl());
        agency.setTimeZone(ZoneId.of(dto.timeZone()));
        agency.setStatus(dto.status());
        return agency;
    }
}

