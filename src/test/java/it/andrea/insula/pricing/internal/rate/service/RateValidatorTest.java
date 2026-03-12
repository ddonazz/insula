package it.andrea.insula.pricing.internal.rate.service;

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
class RateValidatorTest {

    @Mock
    private PropertyQueryService propertyQueryService;

    @InjectMocks
    private RateValidator validator;

    // ─── validateDates ───────────────────────────────────────────────────

    @Test
    void validateDates_shouldPassWhenStartBeforeEnd() {
        assertThatCode(() -> validator.validateDates(
                LocalDate.of(2025, 6, 1),
                LocalDate.of(2025, 8, 31)
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
                LocalDate.of(2025, 8, 31),
                LocalDate.of(2025, 6, 1)
        )).isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void validateDates_shouldPassWhenBothNull() {
        assertThatCode(() -> validator.validateDates(null, null))
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
    void validateUnitExists_shouldPassWhenNull() {
        assertThatCode(() -> validator.validateUnitExists(null))
                .doesNotThrowAnyException();
    }

    // ─── validateStayConstraints ─────────────────────────────────────────

    @Test
    void validateStayConstraints_shouldPassWhenMinLessThanMax() {
        assertThatCode(() -> validator.validateStayConstraints(2, 14))
                .doesNotThrowAnyException();
    }

    @Test
    void validateStayConstraints_shouldPassWhenMinEqualsMax() {
        assertThatCode(() -> validator.validateStayConstraints(7, 7))
                .doesNotThrowAnyException();
    }

    @Test
    void validateStayConstraints_shouldThrowWhenMinGreaterThanMax() {
        assertThatThrownBy(() -> validator.validateStayConstraints(14, 2))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void validateStayConstraints_shouldPassWhenBothNull() {
        assertThatCode(() -> validator.validateStayConstraints(null, null))
                .doesNotThrowAnyException();
    }

    @Test
    void validateStayConstraints_shouldPassWhenOnlyMinProvided() {
        assertThatCode(() -> validator.validateStayConstraints(2, null))
                .doesNotThrowAnyException();
    }

    @Test
    void validateStayConstraints_shouldPassWhenOnlyMaxProvided() {
        assertThatCode(() -> validator.validateStayConstraints(null, 14))
                .doesNotThrowAnyException();
    }
}

