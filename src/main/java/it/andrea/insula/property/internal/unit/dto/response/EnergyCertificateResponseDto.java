package it.andrea.insula.property.internal.unit.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record EnergyCertificateResponseDto(
        UUID publicId,
        String certificateIdentifier,
        String energyClass,
        Double globalPerformanceIndex,
        LocalDate issueDate,
        LocalDate expiryDate
) {
}

