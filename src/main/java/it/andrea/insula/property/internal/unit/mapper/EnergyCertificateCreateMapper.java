package it.andrea.insula.property.internal.unit.mapper;

import it.andrea.insula.property.internal.unit.dto.request.EnergyCertificateCreateDto;
import it.andrea.insula.property.internal.unit.model.EnergyCertificate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.function.Function;

@Component
public class EnergyCertificateCreateMapper implements Function<EnergyCertificateCreateDto, EnergyCertificate> {

    @Override
    public EnergyCertificate apply(EnergyCertificateCreateDto dto) {
        EnergyCertificate cert = new EnergyCertificate();
        cert.setCertificateIdentifier(dto.certificateIdentifier());
        cert.setEnergyClass(dto.energyClass());
        cert.setGlobalPerformanceIndex(dto.globalPerformanceIndex());
        if (dto.issueDate() != null) {
            cert.setIssueDate(LocalDate.parse(dto.issueDate()));
        }
        if (dto.expiryDate() != null) {
            cert.setExpiryDate(LocalDate.parse(dto.expiryDate()));
        }
        return cert;
    }
}

