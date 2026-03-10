package it.andrea.insula.agency.internal.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AgencyCreateDto(

        @NotBlank
        String name,

        @NotBlank
        @Size(min = 11, max = 11)
        String vatNumber,

        @Size(max = 16)
        String fiscalCode,

        @Size(max = 7)
        String sdiCode,

        @Email
        String pecEmail,

        @NotBlank
        @Email
        String contactEmail,

        String phoneNumber,

        String websiteUrl,

        String logoUrl,

        String timeZone

) {
}

