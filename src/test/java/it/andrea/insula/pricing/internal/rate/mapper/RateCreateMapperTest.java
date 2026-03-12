package it.andrea.insula.pricing.internal.rate.mapper;

import it.andrea.insula.pricing.internal.rate.dto.request.RateCreateDto;
import it.andrea.insula.pricing.internal.rate.model.UnitRatePeriod;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RateCreateMapperTest {

    private final RateCreateMapper mapper = new RateCreateMapper();

    @Test
    void apply_shouldMapAllFields() {
        UUID unitId = UUID.randomUUID();
        RateCreateDto dto = new RateCreateDto(
                unitId,
                LocalDate.of(2025, 6, 1), LocalDate.of(2025, 8, 31),
                new BigDecimal("120.00"), new BigDecimal("25.00"),
                2, 14,
                false, true, false,
                Set.of(DayOfWeek.SATURDAY), Set.of(DayOfWeek.SATURDAY)
        );

        UnitRatePeriod result = mapper.apply(dto);

        assertThat(result.getUnitPublicId()).isEqualTo(unitId);
        assertThat(result.getStartDate()).isEqualTo(LocalDate.of(2025, 6, 1));
        assertThat(result.getEndDate()).isEqualTo(LocalDate.of(2025, 8, 31));
        assertThat(result.getPricePerNight()).isEqualByComparingTo("120.00");
        assertThat(result.getExtraGuestPrice()).isEqualByComparingTo("25.00");
        assertThat(result.getMinStay()).isEqualTo(2);
        assertThat(result.getMaxStay()).isEqualTo(14);
        assertThat(result.isStopSell()).isFalse();
        assertThat(result.isClosedToArrival()).isTrue();
        assertThat(result.isClosedToDeparture()).isFalse();
        assertThat(result.getAllowedCheckInDays()).containsExactly(DayOfWeek.SATURDAY);
        assertThat(result.getAllowedCheckOutDays()).containsExactly(DayOfWeek.SATURDAY);
    }

    @Test
    void apply_shouldHandleNullOptionalFields() {
        UUID unitId = UUID.randomUUID();
        RateCreateDto dto = new RateCreateDto(
                unitId,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31),
                null, null,
                null, null,
                false, false, false,
                null, null
        );

        UnitRatePeriod result = mapper.apply(dto);

        assertThat(result.getUnitPublicId()).isEqualTo(unitId);
        assertThat(result.getPricePerNight()).isNull();
        assertThat(result.getExtraGuestPrice()).isNull();
        assertThat(result.getMinStay()).isNull();
        assertThat(result.getMaxStay()).isNull();
        assertThat(result.getAllowedCheckInDays()).isEmpty();
        assertThat(result.getAllowedCheckOutDays()).isEmpty();
    }
}

