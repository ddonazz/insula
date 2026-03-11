package it.andrea.insula.owner.internal.owner.service;

import it.andrea.insula.core.exception.ResourceInUseException;
import it.andrea.insula.owner.internal.owner.exception.OwnerErrorCodes;
import it.andrea.insula.owner.internal.owner.model.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OwnerValidator {

    private final OwnerRepository ownerRepository;

    public void validateCreate(String email, String fiscalCode) {
        if (ownerRepository.existsByEmail(email)) {
            throw new ResourceInUseException(OwnerErrorCodes.OWNER_EMAIL_IN_USE, email);
        }
        if (ownerRepository.existsByFiscalCode(fiscalCode)) {
            throw new ResourceInUseException(OwnerErrorCodes.OWNER_FISCAL_CODE_IN_USE, fiscalCode);
        }
    }

    public void validateUpdate(Long id, String email, String originalEmail, String fiscalCode, String originalFiscalCode) {
        if (email != null && !email.equals(originalEmail)) {
            if (ownerRepository.existsByEmailAndIdNot(email, id)) {
                throw new ResourceInUseException(OwnerErrorCodes.OWNER_EMAIL_IN_USE, email);
            }
        }
        if (fiscalCode != null && !fiscalCode.equals(originalFiscalCode)) {
            if (ownerRepository.existsByFiscalCodeAndIdNot(fiscalCode, id)) {
                throw new ResourceInUseException(OwnerErrorCodes.OWNER_FISCAL_CODE_IN_USE, fiscalCode);
            }
        }
    }
}

