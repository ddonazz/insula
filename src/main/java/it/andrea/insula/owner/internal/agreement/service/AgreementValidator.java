package it.andrea.insula.owner.internal.agreement.service;

import it.andrea.insula.core.exception.BusinessRuleException;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.owner.internal.agreement.exception.AgreementErrorCodes;
import it.andrea.insula.property.PropertyQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgreementValidator {

    private final PropertyQueryService propertyQueryService;

    public void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && !startDate.isBefore(endDate)) {
            throw new BusinessRuleException(AgreementErrorCodes.AGREEMENT_DATES_INVALID);
        }
    }

    public void validateUnitExists(UUID unitPublicId) {
        if (unitPublicId != null && !propertyQueryService.unitExistsByPublicId(unitPublicId)) {
            throw new ResourceNotFoundException(AgreementErrorCodes.AGREEMENT_UNIT_NOT_FOUND, unitPublicId);
        }
    }
}
