package it.andrea.insula.property.internal.unit.dto.request;

import jakarta.validation.constraints.Size;

public record EnergyCertificatePatchDto(
        String certificateIdentifier,

        @Size(max = 10)
        String energyClass,

        Double globalPerformanceIndex,

        String issueDate,

        String expiryDate
) {
}

