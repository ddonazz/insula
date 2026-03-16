package it.andrea.insula.pricing.internal.rate.service;

import it.andrea.insula.core.exception.BusinessRuleException;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.pricing.internal.rate.model.UnitRateDay;
import it.andrea.insula.pricing.internal.rate.model.UnitRateDayRepository;
import it.andrea.insula.pricing.internal.season.model.SeasonPeriod;
import it.andrea.insula.pricing.internal.season.model.SeasonPeriodRepository;
import it.andrea.insula.property.PropertyQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateValidatorTest {

    @Mock
    private PropertyQueryService propertyQueryService;

    @Mock
    private SeasonPeriodRepository seasonRepository;

    @Mock
    private UnitRateDayRepository repository;

    @InjectMocks
    private RateValidator validator;

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

    @Test
    void validateSourceSeasonExists_shouldPassWhenFound() {
        UUID seasonPublicId = UUID.randomUUID();
        when(seasonRepository.findByPublicId(seasonPublicId)).thenReturn(java.util.Optional.of(new SeasonPeriod()));

        assertThatCode(() -> validator.validateSourceSeasonExists(seasonPublicId))
                .doesNotThrowAnyException();
    }

    @Test
    void validateSourceSeasonExists_shouldThrowWhenMissing() {
        UUID seasonPublicId = UUID.randomUUID();
        when(seasonRepository.findByPublicId(seasonPublicId)).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> validator.validateSourceSeasonExists(seasonPublicId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void validateNoDuplicate_shouldThrowWhenDuplicateFound() {
        UUID priceListId = UUID.randomUUID();
        UUID unitId = UUID.randomUUID();
        java.time.LocalDate stayDate = java.time.LocalDate.of(2026, 1, 10);
        UnitRateDay found = new UnitRateDay();
        found.setId(10L);

        when(repository.findByPriceListPublicIdAndUnitPublicIdAndStayDate(priceListId, unitId, stayDate))
                .thenReturn(java.util.Optional.of(found));

        assertThatThrownBy(() -> validator.validateNoDuplicate(priceListId, unitId, stayDate, null))
                .isInstanceOf(BusinessRuleException.class);
    }
}

