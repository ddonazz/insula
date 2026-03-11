package it.andrea.insula.property.internal.unit.mapper;

import it.andrea.insula.property.internal.unit.dto.request.EnergyCertificateUpdateDto;
import it.andrea.insula.property.internal.unit.model.EnergyCertificate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.function.BiFunction;

@Component
public class EnergyCertificateUpdateMapper implements BiFunction<EnergyCertificateUpdateDto, EnergyCertificate, EnergyCertificate> {

    @Override
    public EnergyCertificate apply(EnergyCertificateUpdateDto dto, EnergyCertificate cert) {
        cert.setCertificateIdentifier(dto.certificateIdentifier());
        cert.setEnergyClass(dto.energyClass());
        cert.setGlobalPerformanceIndex(dto.globalPerformanceIndex());
        cert.setIssueDate(dto.issueDate() != null ? LocalDate.parse(dto.issueDate()) : null);
        cert.setExpiryDate(dto.expiryDate() != null ? LocalDate.parse(dto.expiryDate()) : null);
        return cert;
    }
}

