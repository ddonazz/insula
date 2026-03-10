package it.andrea.insula.property.internal.unit.mapper;

import it.andrea.insula.property.internal.unit.dto.request.EnergyCertificatePatchDto;
import it.andrea.insula.property.internal.unit.model.EnergyCertificate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.function.BiFunction;

@Component
public class EnergyCertificatePatchMapper implements BiFunction<EnergyCertificatePatchDto, EnergyCertificate, EnergyCertificate> {

    @Override
    public EnergyCertificate apply(EnergyCertificatePatchDto dto, EnergyCertificate cert) {
        if (dto == null) return cert;
        if (dto.certificateIdentifier() != null) {
            cert.setCertificateIdentifier(dto.certificateIdentifier());
        }
        if (dto.energyClass() != null) {
            cert.setEnergyClass(dto.energyClass());
        }
        if (dto.globalPerformanceIndex() != null) {
            cert.setGlobalPerformanceIndex(dto.globalPerformanceIndex());
        }
        if (dto.issueDate() != null) {
            cert.setIssueDate(LocalDate.parse(dto.issueDate()));
        }
        if (dto.expiryDate() != null) {
            cert.setExpiryDate(LocalDate.parse(dto.expiryDate()));
        }
        return cert;
    }
}

