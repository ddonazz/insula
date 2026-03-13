package it.andrea.insula.owner.internal.owner.service;

import it.andrea.insula.core.exception.ResourceInUseException;
import it.andrea.insula.owner.internal.owner.dto.request.*;
import it.andrea.insula.owner.internal.owner.exception.OwnerErrorCodes;
import it.andrea.insula.owner.internal.owner.model.BusinessOwner;
import it.andrea.insula.owner.internal.owner.model.BusinessOwnerRepository;
import it.andrea.insula.owner.internal.owner.model.Owner;
import it.andrea.insula.owner.internal.owner.model.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OwnerValidator {

    private final OwnerRepository ownerRepository;
    private final BusinessOwnerRepository businessOwnerRepository;

    public void validateCreate(OwnerCreateDto dto) {
        if (ownerRepository.existsByEmail(dto.email())) {
            throw new ResourceInUseException(OwnerErrorCodes.OWNER_EMAIL_IN_USE, dto.email());
        }
        if (ownerRepository.existsByFiscalCode(dto.fiscalCode())) {
            throw new ResourceInUseException(OwnerErrorCodes.OWNER_FISCAL_CODE_IN_USE, dto.fiscalCode());
        }
        if (dto instanceof BusinessOwnerCreateDto bc) {
            if (businessOwnerRepository.existsByVatNumber(bc.vatNumber())) {
                throw new ResourceInUseException(OwnerErrorCodes.OWNER_VAT_NUMBER_IN_USE, bc.vatNumber());
            }
        }
    }

    public void validateUpdate(OwnerUpdateDto dto, Owner owner) {
        validateEmailUpdate(dto.email(), owner.getEmail(), owner.getId());
        validateFiscalCodeUpdate(dto.fiscalCode(), owner.getFiscalCode(), owner.getId());
        if (dto instanceof BusinessOwnerUpdateDto bc && owner instanceof BusinessOwner bo) {
            validateVatNumberUpdate(bc.vatNumber(), bo.getVatNumber(), bo.getId());
        }
    }

    public void validatePatch(OwnerPatchDto dto, Owner owner) {
        validateEmailUpdate(dto.email(), owner.getEmail(), owner.getId());
        validateFiscalCodeUpdate(dto.fiscalCode(), owner.getFiscalCode(), owner.getId());
        if (dto instanceof BusinessOwnerPatchDto bc && owner instanceof BusinessOwner bo) {
            validateVatNumberUpdate(bc.vatNumber(), bo.getVatNumber(), bo.getId());
        }
    }

    private void validateEmailUpdate(String email, String originalEmail, Long id) {
        if (email != null && !email.equals(originalEmail)) {
            if (ownerRepository.existsByEmailAndIdNot(email, id)) {
                throw new ResourceInUseException(OwnerErrorCodes.OWNER_EMAIL_IN_USE, email);
            }
        }
    }

    private void validateFiscalCodeUpdate(String fiscalCode, String originalFiscalCode, Long id) {
        if (fiscalCode != null && !fiscalCode.equals(originalFiscalCode)) {
            if (ownerRepository.existsByFiscalCodeAndIdNot(fiscalCode, id)) {
                throw new ResourceInUseException(OwnerErrorCodes.OWNER_FISCAL_CODE_IN_USE, fiscalCode);
            }
        }
    }

    private void validateVatNumberUpdate(String vatNumber, String originalVatNumber, Long id) {
        if (vatNumber != null && !vatNumber.equals(originalVatNumber)) {
            if (businessOwnerRepository.existsByVatNumberAndIdNot(vatNumber, id)) {
                throw new ResourceInUseException(OwnerErrorCodes.OWNER_VAT_NUMBER_IN_USE, vatNumber);
            }
        }
    }
}

