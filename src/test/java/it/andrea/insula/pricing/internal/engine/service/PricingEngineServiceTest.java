package it.andrea.insula.pricing.internal.engine.service;

import it.andrea.insula.pricing.internal.core.AdjustmentType;
import it.andrea.insula.pricing.internal.engine.dto.request.AvailabilityQueryDto;
import it.andrea.insula.pricing.internal.engine.dto.request.BestRateRequestDto;
import it.andrea.insula.pricing.internal.engine.dto.request.RateResolveRequestDto;
import it.andrea.insula.pricing.internal.engine.dto.response.AvailabilityDayDto;
import it.andrea.insula.pricing.internal.engine.dto.response.BestRateItemDto;
import it.andrea.insula.pricing.internal.engine.dto.response.RateResolveResponseDto;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListRepository;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListStatus;
import it.andrea.insula.pricing.internal.promotion.model.Promotion;
import it.andrea.insula.pricing.internal.promotion.model.PromotionRepository;
import it.andrea.insula.pricing.internal.promotion.model.PromotionStatus;
import it.andrea.insula.pricing.internal.rate.model.UnitRateDay;
import it.andrea.insula.pricing.internal.rate.model.UnitRateDayRepository;
import it.andrea.insula.pricing.internal.rateplan.model.MealPlan;
import it.andrea.insula.pricing.internal.rateplan.model.RatePlan;
import it.andrea.insula.pricing.internal.rateplan.model.RatePlanRepository;
import it.andrea.insula.pricing.internal.rateplan.model.RatePlanStatus;
import it.andrea.insula.pricing.internal.rule.model.LengthOfStayRule;
import it.andrea.insula.pricing.internal.rule.model.PricingRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricingEngineServiceTest {

    @Mock
    private UnitRateDayRepository unitRateDayRepository;
    @Mock
    private PriceListRepository priceListRepository;
    @Mock
    private RatePlanRepository ratePlanRepository;
    @Mock
    private PricingRuleRepository pricingRuleRepository;
    @Mock
    private PromotionRepository promotionRepository;

    private PricingEngineService service;

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
        priceList.setCurrency("EUR");

        service = new PricingEngineService(
                unitRateDayRepository,
                priceListRepository,
                ratePlanRepository,
                pricingRuleRepository,
                promotionRepository,
                new PricingEngineValidator()
        );
    }

    @Test
    void resolve_shouldApplyRuleAndBestPromotion() {
        UUID ratePlanPublicId = UUID.randomUUID();
        RatePlan plan = new RatePlan();
        plan.setPublicId(ratePlanPublicId);
        plan.setStatus(RatePlanStatus.ACTIVE);
        plan.setAdjustmentType(AdjustmentType.PERCENTAGE);
        plan.setAdjustmentValue(new BigDecimal("10"));

        UnitRateDay day1 = day(LocalDate.now().plusDays(5), "100.00");
        UnitRateDay day2 = day(LocalDate.now().plusDays(6), "100.00");

        LengthOfStayRule losRule = new LengthOfStayRule();
        losRule.setName("LOS -10");
        losRule.setAdjustmentType(AdjustmentType.PERCENTAGE);
        losRule.setAdjustmentValue(new BigDecimal("-10"));
        losRule.setStackable(true);
        losRule.setMinNights(2);

        Promotion promo = new Promotion();
        promo.setName("Promo 15");
        promo.setStatus(PromotionStatus.ACTIVE);
        promo.setDiscountType(AdjustmentType.FLAT);
        promo.setDiscountValue(new BigDecimal("15.00"));
        promo.setBookingFrom(LocalDate.now().minusDays(1));
        promo.setBookingTo(LocalDate.now().plusDays(30));
        promo.setStayFrom(LocalDate.now().plusDays(1));
        promo.setStayTo(LocalDate.now().plusDays(30));

        LocalDate checkIn = LocalDate.now().plusDays(5);
        LocalDate checkOut = LocalDate.now().plusDays(7);

        when(priceListRepository.findByPublicId(priceListPublicId)).thenReturn(Optional.of(priceList));
        when(ratePlanRepository.findByPublicIdAndPriceListPublicId(ratePlanPublicId, priceListPublicId)).thenReturn(Optional.of(plan));
        when(unitRateDayRepository.findByRangeWithSourceSeason(priceListPublicId, unitPublicId, checkIn, checkOut.minusDays(1)))
                .thenReturn(List.of(day1, day2));
        when(pricingRuleRepository.findActiveForRatePlan(priceListPublicId, ratePlanPublicId)).thenReturn(List.of(losRule));
        when(promotionRepository.findApplicable(any(), eq(checkIn), eq(checkOut), eq(priceListPublicId))).thenReturn(List.of(promo));

        RateResolveRequestDto request = RateResolveRequestDto.builder()
                .priceListPublicId(priceListPublicId)
                .unitPublicId(unitPublicId)
                .ratePlanPublicId(ratePlanPublicId)
                .checkIn(checkIn)
                .checkOut(checkOut)
                .guests(2)
                .bookingDate(LocalDate.now())
                .build();

        RateResolveResponseDto result = service.resolve(request);

        assertThat(result.available()).isTrue();
        assertThat(result.subtotalBeforeRules()).isEqualByComparingTo("220.00");
        assertThat(result.subtotalAfterRules()).isEqualByComparingTo("198.00");
        assertThat(result.total()).isEqualByComparingTo("183.00");
        assertThat(result.promotionsApplied()).hasSize(1);
    }

    @Test
    void availability_shouldReturnUnavailableForMissingDay() {
        AvailabilityQueryDto query = AvailabilityQueryDto.builder()
                .priceListPublicId(priceListPublicId)
                .unitPublicId(unitPublicId)
                .from(LocalDate.now().plusDays(10))
                .to(LocalDate.now().plusDays(12))
                .guests(1)
                .build();

        when(priceListRepository.findByPublicId(priceListPublicId)).thenReturn(Optional.of(priceList));
        when(ratePlanRepository.findByPriceListPublicIdAndIsDefaultTrueAndStatusNot(priceListPublicId, RatePlanStatus.DELETED))
                .thenReturn(Optional.empty());
        when(unitRateDayRepository.findByRangeWithSourceSeason(
                priceListPublicId,
                unitPublicId,
                query.from(),
                query.to().minusDays(1)
        )).thenReturn(List.of(day(query.from(), "120.00")));

        List<AvailabilityDayDto> result = service.getAvailability(query);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).available()).isTrue();
        assertThat(result.get(1).available()).isFalse();
    }

    @Test
    void best_shouldPickLowestPlanAndExposeDefaultPlan() {
        UUID defaultPlanId = UUID.randomUUID();
        UUID promoPlanId = UUID.randomUUID();

        RatePlan defaultPlan = new RatePlan();
        defaultPlan.setPublicId(defaultPlanId);
        defaultPlan.setName("Standard");
        defaultPlan.setMealPlan(MealPlan.BREAKFAST);
        defaultPlan.setStatus(RatePlanStatus.ACTIVE);
        defaultPlan.setAdjustmentType(AdjustmentType.FLAT);
        defaultPlan.setAdjustmentValue(new BigDecimal("10.00"));
        defaultPlan.setDefault(true);

        RatePlan promoPlan = new RatePlan();
        promoPlan.setPublicId(promoPlanId);
        promoPlan.setName("Web only");
        promoPlan.setMealPlan(MealPlan.ROOM_ONLY);
        promoPlan.setStatus(RatePlanStatus.ACTIVE);
        promoPlan.setAdjustmentType(AdjustmentType.PERCENTAGE);
        promoPlan.setAdjustmentValue(new BigDecimal("-10"));
        promoPlan.setDefault(false);

        LocalDate checkIn = LocalDate.now().plusDays(8);
        LocalDate checkOut = LocalDate.now().plusDays(10);

        when(priceListRepository.findByPublicId(priceListPublicId)).thenReturn(Optional.of(priceList));
        when(ratePlanRepository.findByPriceListPublicIdAndStatusNot(priceListPublicId, RatePlanStatus.DELETED))
                .thenReturn(List.of(defaultPlan, promoPlan));
        when(unitRateDayRepository.findByRangeWithSourceSeason(priceListPublicId, unitPublicId, checkIn, checkOut.minusDays(1)))
                .thenReturn(List.of(day(checkIn, "100.00"), day(checkIn.plusDays(1), "100.00")));
        when(pricingRuleRepository.findActiveForRatePlan(eq(priceListPublicId), any(UUID.class))).thenReturn(List.of());
        when(promotionRepository.findApplicable(any(), eq(checkIn), eq(checkOut), eq(priceListPublicId))).thenReturn(List.of());

        BestRateRequestDto request = BestRateRequestDto.builder()
                .priceListPublicId(priceListPublicId)
                .unitPublicIds(List.of(unitPublicId))
                .checkIn(checkIn)
                .checkOut(checkOut)
                .guests(2)
                .bookingDate(LocalDate.now())
                .build();

        List<BestRateItemDto> result = service.best(request);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().available()).isTrue();
        assertThat(result.getFirst().bestPlan().ratePlanPublicId()).isEqualTo(promoPlanId);
        assertThat(result.getFirst().defaultPlan().ratePlanPublicId()).isEqualTo(defaultPlanId);
    }

    private UnitRateDay day(LocalDate date, String price) {
        UnitRateDay day = new UnitRateDay();
        day.setStayDate(date);
        day.setPricePerNight(new BigDecimal(price));
        day.setStopSell(false);
        day.setClosedToArrival(false);
        day.setClosedToDeparture(false);
        return day;
    }
}

