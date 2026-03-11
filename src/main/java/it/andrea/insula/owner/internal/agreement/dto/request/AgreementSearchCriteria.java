package it.andrea.insula.owner.internal.agreement.dto.request;

import it.andrea.insula.owner.internal.agreement.model.AgreementState;

import java.time.LocalDate;
import java.util.UUID;

public record AgreementSearchCriteria(
        AgreementState state,
        LocalDate startDateFrom,
        LocalDate startDateTo,
        UUID unitPublicId
) {
}

