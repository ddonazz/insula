package it.andrea.insula.pricing.internal.season.service;

import it.andrea.insula.core.exception.BusinessRuleException;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListRepository;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListStatus;
import it.andrea.insula.pricing.internal.rate.model.UnitRateDay;
import it.andrea.insula.pricing.internal.rate.model.UnitRateDayRepository;
import it.andrea.insula.pricing.internal.season.dto.request.SeasonGenerateDto;
import it.andrea.insula.pricing.internal.season.dto.response.SeasonGenerateResultDto;
import it.andrea.insula.pricing.internal.season.model.SeasonPeriod;
import it.andrea.insula.pricing.internal.season.model.SeasonPeriodRepository;
import it.andrea.insula.pricing.internal.season.model.SeasonStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeasonGenerationServiceTest {

    @Mock
    private SeasonPeriodRepository seasonRepository;
    @Mock
    private UnitRateDayRepository unitRateDayRepository;
    @Mock
    private PriceListRepository priceListRepository;

    @InjectMocks
    private SeasonGenerationService service;

    private UUID priceListPublicId;
    private UUID seasonPublicId;
    private UUID unitPublicId;
    private PriceList priceList;
    private SeasonPeriod season;

    @BeforeEach
    void setUp() {
        priceListPublicId = UUID.randomUUID();
        seasonPublicId = UUID.randomUUID();
        unitPublicId = UUID.randomUUID();

        priceList = new PriceList();
        priceList.setPublicId(priceListPublicId);
        priceList.setStatus(PriceListStatus.ACTIVE);

        season = new SeasonPeriod();
        season.setId(99L);
        season.setPublicId(seasonPublicId);
        season.setPriceList(priceList);
        season.setStartDate(LocalDate.of(2026, 7, 1));
        season.setEndDate(LocalDate.of(2026, 7, 3));
        season.setStatus(SeasonStatus.ACTIVE);
    }

    @Test
    void generate_shouldSkipManualDaysWhenOverwriteDisabled() {
        SeasonGenerateDto dto = SeasonGenerateDto.builder()
                .unitPublicIds(List.of(unitPublicId))
                .pricePerNight(new BigDecimal("100.00"))
                .extraGuestPrice(new BigDecimal("20.00"))
                .minStay(2)
                .maxStay(7)
                .overwriteManual(false)
                .build();

        UnitRateDay manual = new UnitRateDay();
        manual.setId(1L);
        manual.setUnitPublicId(unitPublicId);
        manual.setStayDate(LocalDate.of(2026, 7, 1));
        manual.setSourceSeason(null);

        UnitRateDay generated = new UnitRateDay();
        generated.setId(2L);
        generated.setUnitPublicId(unitPublicId);
        generated.setStayDate(LocalDate.of(2026, 7, 2));
        generated.setSourceSeason(season);

        when(priceListRepository.findByPublicId(priceListPublicId)).thenReturn(Optional.of(priceList));
        when(seasonRepository.findByPublicId(seasonPublicId)).thenReturn(Optional.of(season));
        when(unitRateDayRepository.findByPriceListPublicIdAndUnitPublicIdAndStayDateBetweenOrderByStayDate(
                priceListPublicId,
                unitPublicId,
                season.getStartDate(),
                season.getEndDate()
        )).thenReturn(List.of(manual, generated));
        when(unitRateDayRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        SeasonGenerateResultDto result = service.generate(priceListPublicId, seasonPublicId, dto);

        ArgumentCaptor<List<UnitRateDay>> captor = ArgumentCaptor.forClass(List.class);
        verify(unitRateDayRepository).saveAll(captor.capture());

        assertThat(captor.getValue()).hasSize(2);
        assertThat(result.generated()).isEqualTo(2);
        assertThat(result.skippedManual()).isEqualTo(1);
        assertThat(result.unitsProcessed()).isEqualTo(1);
    }

    @Test
    void generate_shouldDeletePreviousSeasonRowsWhenOverwriteEnabled() {
        SeasonGenerateDto dto = SeasonGenerateDto.builder()
                .unitPublicIds(List.of(unitPublicId))
                .pricePerNight(new BigDecimal("130.00"))
                .overwriteManual(true)
                .build();

        when(priceListRepository.findByPublicId(priceListPublicId)).thenReturn(Optional.of(priceList));
        when(seasonRepository.findByPublicId(seasonPublicId)).thenReturn(Optional.of(season));
        when(unitRateDayRepository.findByPriceListPublicIdAndUnitPublicIdAndStayDateBetweenOrderByStayDate(
                priceListPublicId,
                unitPublicId,
                season.getStartDate(),
                season.getEndDate()
        )).thenReturn(List.of());
        when(unitRateDayRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        service.generate(priceListPublicId, seasonPublicId, dto);

        verify(unitRateDayRepository).deleteBySourceSeasonIdAndUnitPublicId(season.getId(), unitPublicId);
    }

    @Test
    void generate_shouldFailWhenSeasonDoesNotBelongToPriceList() {
        PriceList anotherPriceList = new PriceList();
        anotherPriceList.setPublicId(UUID.randomUUID());

        season.setPriceList(anotherPriceList);

        SeasonGenerateDto dto = SeasonGenerateDto.builder()
                .unitPublicIds(List.of(unitPublicId))
                .pricePerNight(new BigDecimal("90.00"))
                .build();

        when(priceListRepository.findByPublicId(priceListPublicId)).thenReturn(Optional.of(priceList));
        when(seasonRepository.findByPublicId(seasonPublicId)).thenReturn(Optional.of(season));

        assertThatThrownBy(() -> service.generate(priceListPublicId, seasonPublicId, dto))
                .isInstanceOf(BusinessRuleException.class);
    }
}

