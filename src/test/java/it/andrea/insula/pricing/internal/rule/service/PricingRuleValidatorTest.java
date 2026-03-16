package it.andrea.insula.pricing.internal.rule.service;

import it.andrea.insula.core.exception.BusinessRuleException;
import it.andrea.insula.pricing.internal.rule.dto.request.PricingRuleType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PricingRuleValidatorTest {

    private final PricingRuleValidator validator = new PricingRuleValidator();

    @Test
    void validateCommon_shouldPassWhenValid() {
        assertThatCode(() -> validator.validateCommon(new BigDecimal("10.00"), 0)).doesNotThrowAnyException();
    }

    @Test
    void validateCommon_shouldThrowWhenAdjustmentValueNull() {
        assertThatThrownBy(() -> validator.validateCommon(null, 0)).isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void validateCommon_shouldThrowWhenPriorityNegative() {
        assertThatThrownBy(() -> validator.validateCommon(BigDecimal.ONE, -1)).isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void validateByType_shouldPassForLosWhenOneBoundaryProvided() {
        assertThatCode(() -> validator.validateByType(PricingRuleType.LOS, 7, null, null, null, null, null, null))
                .doesNotThrowAnyException();
    }

    @Test
    void validateByType_shouldThrowForLosWhenNoBoundaries() {
        assertThatThrownBy(() -> validator.validateByType(PricingRuleType.LOS, null, null, null, null, null, null, null))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void validateByType_shouldPassForLeadTime() {
        assertThatCode(() -> validator.validateByType(PricingRuleType.LEAD_TIME, null, null, 10, null, null, null, null))
                .doesNotThrowAnyException();
    }

    @Test
    void validateByType_shouldThrowForDayOfWeekWhenEmptySet() {
        assertThatThrownBy(() -> validator.validateByType(PricingRuleType.DAY_OF_WEEK, null, null, null, null, Set.of(), null, null))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void validateByType_shouldPassForDayOfWeek() {
        assertThatCode(() -> validator.validateByType(PricingRuleType.DAY_OF_WEEK, null, null, null, null,
                Set.of(DayOfWeek.FRIDAY), null, null)).doesNotThrowAnyException();
    }

    @Test
    void validateByType_shouldThrowForOccupancyWhenThresholdInvalid() {
        assertThatThrownBy(() -> validator.validateByType(PricingRuleType.OCCUPANCY, null, null, null, null, null, 0, null))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void validateByType_shouldPassForMinStay() {
        assertThatCode(() -> validator.validateByType(PricingRuleType.MIN_STAY, null, null, null, null, null, null, 2))
                .doesNotThrowAnyException();
    }
}

