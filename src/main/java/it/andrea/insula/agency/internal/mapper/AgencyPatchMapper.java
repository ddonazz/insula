package it.andrea.insula.agency.internal.mapper;

import it.andrea.insula.agency.internal.dto.request.AgencyPatchDto;
import it.andrea.insula.agency.internal.model.Agency;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.function.BiFunction;

@Component
public class AgencyPatchMapper implements BiFunction<AgencyPatchDto, Agency, Agency> {

    @Override
    public Agency apply(AgencyPatchDto dto, Agency agency) {
        if (dto.name() != null) {
            agency.setName(dto.name());
        }
        if (dto.vatNumber() != null) {
            agency.setVatNumber(dto.vatNumber());
        }
        if (dto.fiscalCode() != null) {
            agency.setFiscalCode(dto.fiscalCode());
        }
        if (dto.sdiCode() != null) {
            agency.setSdiCode(dto.sdiCode());
        }
        if (dto.pecEmail() != null) {
            agency.setPecEmail(dto.pecEmail());
        }
        if (dto.contactEmail() != null) {
            agency.setContactEmail(dto.contactEmail());
        }
        if (dto.phoneNumber() != null) {
            agency.setPhoneNumber(dto.phoneNumber());
        }
        if (dto.websiteUrl() != null) {
            agency.setWebsiteUrl(dto.websiteUrl());
        }
        if (dto.logoUrl() != null) {
            agency.setLogoUrl(dto.logoUrl());
        }
        if (dto.timeZone() != null) {
            agency.setTimeZone(ZoneId.of(dto.timeZone()));
        }
        if (dto.status() != null) {
            agency.setStatus(dto.status());
        }
        return agency;
    }
}

