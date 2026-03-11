package it.andrea.insula.owner.internal.owner.service;

import it.andrea.insula.core.exception.ResourceInUseException;
import it.andrea.insula.owner.internal.owner.model.OwnerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OwnerValidatorTest {

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private OwnerValidator validator;

    // ─── validateCreate ──────────────────────────────────────────────────

    @Test
    void validateCreate_shouldPassWhenEmailAndFiscalCodeAreUnique() {
        when(ownerRepository.existsByEmail("mario@rossi.it")).thenReturn(false);
        when(ownerRepository.existsByFiscalCode("RSSMRA80A01H501Z")).thenReturn(false);

        assertThatCode(() -> validator.validateCreate("mario@rossi.it", "RSSMRA80A01H501Z"))
                .doesNotThrowAnyException();
    }

    @Test
    void validateCreate_shouldThrowWhenEmailInUse() {
        when(ownerRepository.existsByEmail("mario@rossi.it")).thenReturn(true);

        assertThatThrownBy(() -> validator.validateCreate("mario@rossi.it", "RSSMRA80A01H501Z"))
                .isInstanceOf(ResourceInUseException.class);
    }

    @Test
    void validateCreate_shouldThrowWhenFiscalCodeInUse() {
        when(ownerRepository.existsByEmail("mario@rossi.it")).thenReturn(false);
        when(ownerRepository.existsByFiscalCode("RSSMRA80A01H501Z")).thenReturn(true);

        assertThatThrownBy(() -> validator.validateCreate("mario@rossi.it", "RSSMRA80A01H501Z"))
                .isInstanceOf(ResourceInUseException.class);
    }

    // ─── validateUpdate ──────────────────────────────────────────────────

    @Test
    void validateUpdate_shouldPassWhenEmailUnchanged() {
        assertThatCode(() -> validator.validateUpdate(1L, "same@email.it", "same@email.it", "RSSMRA80A01H501Z", "RSSMRA80A01H501Z"))
                .doesNotThrowAnyException();
    }

    @Test
    void validateUpdate_shouldThrowWhenNewEmailInUse() {
        when(ownerRepository.existsByEmailAndIdNot("new@email.it", 1L)).thenReturn(true);

        assertThatThrownBy(() -> validator.validateUpdate(1L, "new@email.it", "old@email.it", "RSSMRA80A01H501Z", "RSSMRA80A01H501Z"))
                .isInstanceOf(ResourceInUseException.class);
    }

    @Test
    void validateUpdate_shouldPassWhenNewEmailAvailable() {
        when(ownerRepository.existsByEmailAndIdNot("new@email.it", 1L)).thenReturn(false);

        assertThatCode(() -> validator.validateUpdate(1L, "new@email.it", "old@email.it", "RSSMRA80A01H501Z", "RSSMRA80A01H501Z"))
                .doesNotThrowAnyException();
    }

    @Test
    void validateUpdate_shouldThrowWhenNewFiscalCodeInUse() {
        when(ownerRepository.existsByFiscalCodeAndIdNot("NEWCOD80A01H501Z", 1L)).thenReturn(true);

        assertThatThrownBy(() -> validator.validateUpdate(1L, "same@email.it", "same@email.it", "NEWCOD80A01H501Z", "RSSMRA80A01H501Z"))
                .isInstanceOf(ResourceInUseException.class);
    }

    @Test
    void validateUpdate_shouldPassWhenNullEmailAndFiscalCode() {
        assertThatCode(() -> validator.validateUpdate(1L, null, "old@email.it", null, "RSSMRA80A01H501Z"))
                .doesNotThrowAnyException();
    }
}

