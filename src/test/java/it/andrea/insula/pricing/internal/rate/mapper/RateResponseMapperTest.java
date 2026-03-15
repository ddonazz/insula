package it.andrea.insula.pricing.internal.rate.mapper;

import it.andrea.insula.core.dto.EnumTranslator;
import it.andrea.insula.core.dto.TranslatedEnum;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import it.andrea.insula.pricing.internal.rate.dto.response.RateResponseDto;
import it.andrea.insula.pricing.internal.rate.model.UnitRatePeriod;
import it.andrea.insula.property.PropertyQueryService;
import it.andrea.insula.property.UnitSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateResponseMapperTest {

    @Mock
    private EnumTranslator enumTranslator;

    @Mock
    private PropertyQueryService propertyQueryService;

    private RateResponseMapper mapper;
    private UnitRatePeriod rate;
    private UUID unitPublicId;
    private UUID priceListPublicId;

    @BeforeEach
    void setUp() {
        mapper = new RateResponseMapper(enumTranslator, propertyQueryService);

        unitPublicId = UUID.randomUUID();
        priceListPublicId = UUID.randomUUID();

        PriceList priceList = new PriceList();
        priceList.setPublicId(priceListPublicId);
        priceList.setName("Summer 2025");

        rate = new UnitRatePeriod();
        rate.setPublicId(UUID.randomUUID());
        rate.setPriceList(priceList);
        rate.setUnitPublicId(unitPublicId);
        rate.setStartDate(LocalDate.of(2025, 6, 1));
        rate.setEndDate(LocalDate.of(2025, 8, 31));
        rate.setPricePerNight(new BigDecimal("120.00"));
        rate.setExtraGuestPrice(new BigDecimal("25.00"));
        rate.setMinStay(2);
        rate.setMaxStay(14);
        rate.setStopSell(false);
        rate.setClosedToArrival(true);
        rate.setClosedToDeparture(false);
        rate.setAllowedCheckInDays(Set.of(DayOfWeek.SATURDAY));
        rate.setAllowedCheckOutDays(Set.of(DayOfWeek.SATURDAY));
    }

    @Test
    void apply_shouldMapAllFields() {
        TranslatedEnum saturday = new TranslatedEnum("SATURDAY", "Sabato");

        UnitSummary unitSummary = UnitSummary.builder()
                .publicId(unitPublicId)
                .propertyPublicId(UUID.randomUUID())
                .propertyName("Residenza Mare")
                .internalName("App. 3B")
                .type("APARTMENT")
                .floor("3")
                .internalNumber("B")
                .build();

        when(enumTranslator.translateAll(Set.of(DayOfWeek.SATURDAY))).thenReturn(Set.of(saturday));
        when(propertyQueryService.findUnitByPublicId(unitPublicId)).thenReturn(Optional.of(unitSummary));

        RateResponseDto result = mapper.apply(rate);

        assertThat(result.publicId()).isEqualTo(rate.getPublicId());
        assertThat(result.priceListPublicId()).isEqualTo(priceListPublicId);
        assertThat(result.priceListName()).isEqualTo("Summer 2025");
        assertThat(result.unitPublicId()).isEqualTo(unitPublicId);
        assertThat(result.startDate()).isEqualTo(LocalDate.of(2025, 6, 1));
        assertThat(result.endDate()).isEqualTo(LocalDate.of(2025, 8, 31));
        assertThat(result.pricePerNight()).isEqualByComparingTo("120.00");
        assertThat(result.extraGuestPrice()).isEqualByComparingTo("25.00");
        assertThat(result.minStay()).isEqualTo(2);
        assertThat(result.maxStay()).isEqualTo(14);
        assertThat(result.stopSell()).isFalse();
        assertThat(result.closedToArrival()).isTrue();
        assertThat(result.closedToDeparture()).isFalse();
        assertThat(result.allowedCheckInDays()).containsExactly(saturday);
        assertThat(result.allowedCheckOutDays()).containsExactly(saturday);
        // Unit summary
        assertThat(result.unit()).isNotNull();
        assertThat(result.unit().internalName()).isEqualTo("App. 3B");
        assertThat(result.unit().propertyName()).isEqualTo("Residenza Mare");
    }

    @Test
    void apply_shouldHandleUnitNotFound() {
        TranslatedEnum saturday = new TranslatedEnum("SATURDAY", "Saturday");

        when(enumTranslator.translateAll(Set.of(DayOfWeek.SATURDAY))).thenReturn(Set.of(saturday));
        when(propertyQueryService.findUnitByPublicId(unitPublicId)).thenReturn(Optional.empty());

        RateResponseDto result = mapper.apply(rate);

        assertThat(result.unit()).isNull();
        assertThat(result.unitPublicId()).isEqualTo(unitPublicId);
    }

    @Test
    void apply_shouldHandleNullOptionalFields() {
        rate.setPricePerNight(null);
        rate.setExtraGuestPrice(null);
        rate.setMinStay(null);
        rate.setMaxStay(null);

        when(enumTranslator.translateAll(Set.of(DayOfWeek.SATURDAY)))
                .thenReturn(Set.of(new TranslatedEnum("SATURDAY", "Saturday")));
        when(propertyQueryService.findUnitByPublicId(unitPublicId)).thenReturn(Optional.empty());

        RateResponseDto result = mapper.apply(rate);

        assertThat(result.pricePerNight()).isNull();
        assertThat(result.extraGuestPrice()).isNull();
        assertThat(result.minStay()).isNull();
        assertThat(result.maxStay()).isNull();
    }
}

