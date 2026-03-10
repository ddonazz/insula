package it.andrea.insula.agency.internal.dto.request;

import it.andrea.insula.agency.internal.model.AgencyStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record AgencyUpdateDto(

        String name,

        @Size(min = 11, max = 11)
        String vatNumber,

        @Size(max = 16)
        String fiscalCode,

        @Size(max = 7)
        String sdiCode,

        @Email
        String pecEmail,

        @Email
        String contactEmail,

        String phoneNumber,

        String websiteUrl,

        String logoUrl,

        String timeZone,

        AgencyStatus status

) {
}

