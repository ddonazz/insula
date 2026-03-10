package it.andrea.insula.agency.internal.service;

import it.andrea.insula.agency.internal.exception.AgencyErrorCodes;
import it.andrea.insula.agency.internal.model.AgencyRepository;
import it.andrea.insula.core.exception.ResourceInUseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgencyValidator {

    private final AgencyRepository agencyRepository;

    public void validateCreate(String vatNumber, String pecEmail) {
        if (agencyRepository.existsByVatNumber(vatNumber)) {
            throw new ResourceInUseException(AgencyErrorCodes.VAT_NUMBER_ALREADY_EXISTS, vatNumber);
        }
        if (pecEmail != null && agencyRepository.existsByPecEmail(pecEmail)) {
            throw new ResourceInUseException(AgencyErrorCodes.PEC_EMAIL_ALREADY_EXISTS, pecEmail);
        }
    }

    public void validateUpdate(Long id, String vatNumber, String originalVatNumber, String pecEmail, String originalPecEmail) {
        if (vatNumber != null && !vatNumber.equals(originalVatNumber)) {
            if (agencyRepository.existsByVatNumberAndIdNot(vatNumber, id)) {
                throw new ResourceInUseException(AgencyErrorCodes.VAT_NUMBER_ALREADY_EXISTS, vatNumber);
            }
        }
        if (pecEmail != null && !pecEmail.equals(originalPecEmail)) {
            if (agencyRepository.existsByPecEmailAndIdNot(pecEmail, id)) {
                throw new ResourceInUseException(AgencyErrorCodes.PEC_EMAIL_ALREADY_EXISTS, pecEmail);
            }
        }
    }
}

