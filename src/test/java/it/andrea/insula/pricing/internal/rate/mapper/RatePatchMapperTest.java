package it.andrea.insula.pricing.internal.rate.mapper;

import it.andrea.insula.pricing.internal.rate.dto.request.RatePatchDto;
import it.andrea.insula.pricing.internal.rate.model.UnitRatePeriod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RatePatchMapperTest {

    private final RatePatchMapper mapper = new RatePatchMapper();
    private UnitRatePeriod rate;

    @BeforeEach
    void setUp() {
        rate = new UnitRatePeriod();
        rate.setUnitPublicId(UUID.randomUUID());
        rate.setStartDate(LocalDate.of(2025, 6, 1));
        rate.setEndDate(LocalDate.of(2025, 8, 31));
        rate.setPricePerNight(new BigDecimal("100.00"));
        rate.setExtraGuestPrice(new BigDecimal("20.00"));
        rate.setMinStay(2);
        rate.setMaxStay(14);
        rate.setStopSell(false);
        rate.setClosedToArrival(false);
        rate.setClosedToDeparture(false);
    }

    @Test
    void apply_shouldUpdateAllFieldsWhenProvided() {
        UUID newUnit = UUID.randomUUID();
        RatePatchDto dto = new RatePatchDto(
                newUnit,
                LocalDate.of(2025, 7, 1), LocalDate.of(2025, 9, 30),
                new BigDecimal("150.00"), new BigDecimal("30.00"),
                3, 21,
                true, true, true,
                Set.of(DayOfWeek.MONDAY), Set.of(DayOfWeek.FRIDAY)
        );

        UnitRatePeriod result = mapper.apply(dto, rate);

        assertThat(result.getUnitPublicId()).isEqualTo(newUnit);
        assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2025, 7, 1));
        assertThat(result.getEndDate()).isEqualTo(LocalDate.of(2025, 9, 30));
        assertThat(result.getPricePerNight()).isEqualByComparingTo("150.00");
        assertThat(result.getExtraGuestPrice()).isEqualByComparingTo("30.00");
        assertThat(result.getMinStay()).isEqualTo(3);
        assertThat(result.getMaxStay()).isEqualTo(21);
        assertThat(result.isStopSell()).isTrue();
        assertThat(result.isClosedToArrival()).isTrue();
        assertThat(result.isClosedToDeparture()).isTrue();
        assertThat(result.getAllowedCheckInDays()).containsExactly(DayOfWeek.MONDAY);
        assertThat(result.getAllowedCheckOutDays()).containsExactly(DayOfWeek.FRIDAY);
    }

    @Test
    void apply_shouldSkipNullFields() {
        UUID originalUnit = rate.getUnitPublicId();
        RatePatchDto dto = new RatePatchDto(
                null, null, null, null, null,
                null, null, null, null, null, null, null
        );

        UnitRatePeriod result = mapper.apply(dto, rate);

        assertThat(result.getUnitPublicId()).isEqualTo(originalUnit);
        assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2025, 6, 1));
        assertThat(result.getEndDate()).isEqualTo(LocalDate.of(2025, 8, 31));
        assertThat(result.getPricePerNight()).isEqualByComparingTo("100.00");
        assertThat(result.getMinStay()).isEqualTo(2);
        assertThat(result.getMaxStay()).isEqualTo(14);
        assertThat(result.isStopSell()).isFalse();
    }

    @Test
    void apply_shouldUpdateOnlyPrice() {
        RatePatchDto dto = new RatePatchDto(
                null, null, null, new BigDecimal("200.00"), null,
                null, null, null, null, null, null, null
        );

        UnitRatePeriod result = mapper.apply(dto, rate);

        assertThat(result.getPricePerNight()).isEqualByComparingTo("200.00");
        assertThat(result.getExtraGuestPrice()).isEqualByComparingTo("20.00"); // preserved
        assertThat(result.getMinStay()).isEqualTo(2); // preserved
    }

    @Test
    void apply_shouldReturnSameInstance() {
        RatePatchDto dto = new RatePatchDto(
                null, null, null, null, null,
                null, null, null, null, null, null, null
        );

        UnitRatePeriod result = mapper.apply(dto, rate);

        assertThat(result).isSameAs(rate);
    }
}

