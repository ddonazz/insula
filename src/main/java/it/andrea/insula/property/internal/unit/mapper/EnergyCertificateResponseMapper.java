package it.andrea.insula.property.internal.unit.mapper;

import it.andrea.insula.property.internal.unit.dto.response.EnergyCertificateResponseDto;
import it.andrea.insula.property.internal.unit.model.EnergyCertificate;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class EnergyCertificateResponseMapper implements Function<EnergyCertificate, EnergyCertificateResponseDto> {

    @Override
    public EnergyCertificateResponseDto apply(EnergyCertificate cert) {
        if (cert == null) return null;
        return EnergyCertificateResponseDto.builder()
                .publicId(cert.getPublicId())
                .certificateIdentifier(cert.getCertificateIdentifier())
                .energyClass(cert.getEnergyClass())
                .globalPerformanceIndex(cert.getGlobalPerformanceIndex())
                .issueDate(cert.getIssueDate())
                .expiryDate(cert.getExpiryDate())
                .build();
    }
}

