package it.andrea.insula.pricing.internal.rate.service;

import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListRepository;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListStatus;
import it.andrea.insula.pricing.internal.rate.dto.request.CalendarBulkPatchDto;
import it.andrea.insula.pricing.internal.rate.dto.request.CalendarDayPatchDto;
import it.andrea.insula.pricing.internal.rate.dto.response.CalendarDayDto;
import it.andrea.insula.pricing.internal.rate.model.UnitRateDay;
import it.andrea.insula.pricing.internal.rate.model.UnitRateDayRepository;
import it.andrea.insula.pricing.internal.season.model.SeasonPeriod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateCalendarBackofficeServiceTest {

    @Mock
    private UnitRateDayRepository unitRateDayRepository;
    @Mock
    private PriceListRepository priceListRepository;

    private RateCalendarBackofficeService service;

    private UUID priceListPublicId;
    private UUID unitPublicId;
    private PriceList priceList;

    @BeforeEach
    void setUp() {
        priceListPublicId = UUID.randomUUID();
        unitPublicId = UUID.randomUUID();

        priceList = new PriceList();
        priceList.setPublicId(priceListPublicId);
        priceList.setStatus(PriceListStatus.ACTIVE);

        service = new RateCalendarBackofficeService(unitRateDayRepository, priceListRepository, new RateCalendarValidator());
    }

    @Test
    void getCalendar_shouldMapSeasonSource() {
        SeasonPeriod season = new SeasonPeriod();
        season.setName("Summer");

        UnitRateDay day = new UnitRateDay();
        day.setStayDate(LocalDate.of(2026, 8, 10));
        day.setPricePerNight(new BigDecimal("150.00"));
        day.setSourceSeason(season);

        when(priceListRepository.findByPublicId(priceListPublicId)).thenReturn(Optional.of(priceList));
        when(unitRateDayRepository.findByRangeWithSourceSeason(
                priceListPublicId,
                unitPublicId,
                LocalDate.of(2026, 8, 10),
                LocalDate.of(2026, 8, 10)
        )).thenReturn(List.of(day));

        List<CalendarDayDto> result = service.getCalendar(
                priceListPublicId,
                unitPublicId,
                LocalDate.of(2026, 8, 10),
                LocalDate.of(2026, 8, 11)
        );

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().source()).isEqualTo("SEASON");
        assertThat(result.getFirst().seasonName()).isEqualTo("Summer");
    }

    @Test
    void bulkPatch_shouldCreateMissingDaysAndReturnUpdatedCount() {
        CalendarBulkPatchDto dto = CalendarBulkPatchDto.builder()
                .unitPublicIds(List.of(unitPublicId))
                .from(LocalDate.of(2026, 9, 1))
                .after(LocalDate.of(2026, 9, 3))
                .patch(CalendarDayPatchDto.builder().pricePerNight(new BigDecimal("99.99")).build())
                .build();

        when(priceListRepository.findByPublicId(priceListPublicId)).thenReturn(Optional.of(priceList));
        when(unitRateDayRepository.findByPriceListPublicIdAndUnitPublicIdInAndStayDateBetween(
                priceListPublicId,
                List.of(unitPublicId),
                dto.from(),
                dto.after().minusDays(1)
        )).thenReturn(List.of());
        when(unitRateDayRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        int updated = service.bulkPatch(priceListPublicId, dto);

        ArgumentCaptor<List<UnitRateDay>> captor = ArgumentCaptor.forClass(List.class);
        verify(unitRateDayRepository).saveAll(captor.capture());

        assertThat(updated).isEqualTo(2);
        assertThat(captor.getValue()).hasSize(2);
        assertThat(captor.getValue()).allMatch(day -> day.getPricePerNight().compareTo(new BigDecimal("99.99")) == 0);
        assertThat(captor.getValue()).allMatch(day -> day.getSourceSeason() == null);
    }

    @Test
    void patchDay_shouldResetSourceSeasonToManual() {
        UnitRateDay existing = new UnitRateDay();
        existing.setStayDate(LocalDate.of(2026, 10, 1));
        existing.setSourceSeason(new SeasonPeriod());

        when(priceListRepository.findByPublicId(priceListPublicId)).thenReturn(Optional.of(priceList));
        when(unitRateDayRepository.findByPriceListPublicIdAndUnitPublicIdAndStayDate(
                priceListPublicId,
                unitPublicId,
                LocalDate.of(2026, 10, 1)
        )).thenReturn(Optional.of(existing));
        when(unitRateDayRepository.save(existing)).thenReturn(existing);

        CalendarDayDto result = service.patchDay(
                priceListPublicId,
                unitPublicId,
                LocalDate.of(2026, 10, 1),
                CalendarDayPatchDto.builder().stopSell(true).build()
        );

        assertThat(existing.getSourceSeason()).isNull();
        assertThat(existing.isStopSell()).isTrue();
        assertThat(result.source()).isEqualTo("MANUAL");
    }
}

