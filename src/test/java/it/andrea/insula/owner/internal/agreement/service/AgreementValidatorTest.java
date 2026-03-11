package it.andrea.insula.owner.internal.agreement.service;

import it.andrea.insula.core.exception.BusinessRuleException;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.property.PropertyQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgreementValidatorTest {

    @Mock
    private PropertyQueryService propertyQueryService;

    @InjectMocks
    private AgreementValidator validator;

    // ─── validateDates ───────────────────────────────────────────────────

    @Test
    void validateDates_shouldPassWhenStartBeforeEnd() {
        assertThatCode(() -> validator.validateDates(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31)
        )).doesNotThrowAnyException();
    }

    @Test
    void validateDates_shouldThrowWhenStartEqualsEnd() {
        assertThatThrownBy(() -> validator.validateDates(
                LocalDate.of(2025, 6, 1),
                LocalDate.of(2025, 6, 1)
        )).isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void validateDates_shouldThrowWhenStartAfterEnd() {
        assertThatThrownBy(() -> validator.validateDates(
                LocalDate.of(2025, 12, 31),
                LocalDate.of(2025, 1, 1)
        )).isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void validateDates_shouldPassWhenBothNull() {
        assertThatCode(() -> validator.validateDates(null, null))
                .doesNotThrowAnyException();
    }

    @Test
    void validateDates_shouldPassWhenStartNull() {
        assertThatCode(() -> validator.validateDates(null, LocalDate.of(2025, 12, 31)))
                .doesNotThrowAnyException();
    }

    // ─── validateUnitExists ──────────────────────────────────────────────

    @Test
    void validateUnitExists_shouldPassWhenUnitExists() {
        UUID unitId = UUID.randomUUID();
        when(propertyQueryService.unitExistsByPublicId(unitId)).thenReturn(true);

        assertThatCode(() -> validator.validateUnitExists(unitId))
                .doesNotThrowAnyException();
    }

    @Test
    void validateUnitExists_shouldThrowWhenUnitNotFound() {
        UUID unitId = UUID.randomUUID();
        when(propertyQueryService.unitExistsByPublicId(unitId)).thenReturn(false);

        assertThatThrownBy(() -> validator.validateUnitExists(unitId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void validateUnitExists_shouldPassWhenUnitIdIsNull() {
        assertThatCode(() -> validator.validateUnitExists(null))
                .doesNotThrowAnyException();
    }
}

