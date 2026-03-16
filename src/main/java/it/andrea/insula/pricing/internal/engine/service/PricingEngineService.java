package it.andrea.insula.pricing.internal.engine.service;

import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.pricing.internal.core.AdjustmentType;
import it.andrea.insula.pricing.internal.engine.dto.request.AvailabilityQueryDto;
import it.andrea.insula.pricing.internal.engine.dto.request.BestRateRequestDto;
import it.andrea.insula.pricing.internal.engine.dto.request.RateResolveRequestDto;
import it.andrea.insula.pricing.internal.engine.dto.response.*;
import it.andrea.insula.pricing.internal.engine.exception.PricingEngineErrorCodes;
import it.andrea.insula.pricing.internal.pricelist.exception.PriceListErrorCodes;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListRepository;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListStatus;
import it.andrea.insula.pricing.internal.promotion.model.Promotion;
import it.andrea.insula.pricing.internal.promotion.model.PromotionRepository;
import it.andrea.insula.pricing.internal.rate.model.UnitRateDay;
import it.andrea.insula.pricing.internal.rate.model.UnitRateDayRepository;
import it.andrea.insula.pricing.internal.rateplan.model.RatePlan;
import it.andrea.insula.pricing.internal.rateplan.model.RatePlanRepository;
import it.andrea.insula.pricing.internal.rateplan.model.RatePlanStatus;
import it.andrea.insula.pricing.internal.rule.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PricingEngineService {

    private static final String UNAVAILABLE_STOP_SELL = "STOP_SELL";
    private static final String UNAVAILABLE_CLOSED_TO_ARRIVAL = "CLOSED_TO_ARRIVAL";
    private static final String UNAVAILABLE_MIN_STAY = "MIN_STAY";
    private static final String UNAVAILABLE_NO_PRICE = "NO_PRICE";

    private final UnitRateDayRepository unitRateDayRepository;
    private final PriceListRepository priceListRepository;
    private final RatePlanRepository ratePlanRepository;
    private final PricingRuleRepository pricingRuleRepository;
    private final PromotionRepository promotionRepository;
    private final PricingEngineValidator validator;

    // TODO: @Cacheable("availability")
    public List<AvailabilityDayDto> getAvailability(AvailabilityQueryDto query) {
        validator.validateAvailabilityRange(query.from(), query.to());
        findActivePriceList(query.priceListPublicId());

        RatePlan ratePlan = resolveRatePlanForAvailability(query.priceListPublicId(), query.ratePlanPublicId());
        List<UnitRateDay> existingDays = unitRateDayRepository.findByRangeWithSourceSeason(
                query.priceListPublicId(),
                query.unitPublicId(),
                query.from(),
                query.to().minusDays(1)
        );

        Map<LocalDate, UnitRateDay> dayMap = new HashMap<>();
        for (UnitRateDay day : existingDays) {
            dayMap.put(day.getStayDate(), day);
        }

        List<AvailabilityDayDto> result = new ArrayList<>();
        for (LocalDate date = query.from(); date.isBefore(query.to()); date = date.plusDays(1)) {
            UnitRateDay day = dayMap.get(date);
            if (day == null || day.getPricePerNight() == null || day.isStopSell()) {
                result.add(AvailabilityDayDto.builder()
                        .date(date)
                        .available(false)
                        .closedToArrival(day != null && day.isClosedToArrival())
                        .closedToDeparture(day != null && day.isClosedToDeparture())
                        .minStay(day != null ? day.getMinStay() : null)
                        .build());
                continue;
            }

            BigDecimal base = day.getPricePerNight();
            BigDecimal finalPrice = ratePlan != null ? scaleMoney(ratePlan.applyTo(base)) : scaleMoney(base);
            result.add(AvailabilityDayDto.builder()
                    .date(date)
                    .available(true)
                    .closedToArrival(day.isClosedToArrival())
                    .closedToDeparture(day.isClosedToDeparture())
                    .minStay(day.getMinStay())
                    .basePrice(scaleMoney(base))
                    .price(finalPrice)
                    .ratePlanAdjustment(scaleMoney(finalPrice.subtract(base)))
                    .build());
        }

        return result;
    }

    public RateResolveResponseDto resolve(RateResolveRequestDto request) {
        validator.validateStayDates(request.checkIn(), request.checkOut());
        validator.validateFutureCheckout(request.checkOut(), LocalDate.now());

        PriceList priceList = findActivePriceList(request.priceListPublicId());
        RatePlan ratePlan = findActiveRatePlan(request.priceListPublicId(), request.ratePlanPublicId());

        InternalResolveResult result = resolveInternal(
                priceList,
                request.unitPublicId(),
                ratePlan,
                request.checkIn(),
                request.checkOut(),
                request.guests(),
                request.bookingDate()
        );

        return toResolveResponse(priceList, result);
    }

    public List<BestRateItemDto> best(BestRateRequestDto request) {
        validator.validateStayDates(request.checkIn(), request.checkOut());
        validator.validateFutureCheckout(request.checkOut(), LocalDate.now());

        PriceList priceList = findActivePriceList(request.priceListPublicId());
        List<RatePlan> activePlans = ratePlanRepository.findByPriceListPublicIdAndStatusNot(
                request.priceListPublicId(),
                RatePlanStatus.DELETED
        ).stream().filter(plan -> plan.getStatus() == RatePlanStatus.ACTIVE).toList();

        if (activePlans.isEmpty()) {
            throw new ResourceNotFoundException(PricingEngineErrorCodes.RESOLVE_PLAN_NOT_FOUND, request.priceListPublicId());
        }

        List<BestRateItemDto> items = new ArrayList<>();
        for (UUID unitPublicId : request.unitPublicIds().stream().distinct().toList()) {
            List<PlanResolveProjection> projections = new ArrayList<>();
            for (RatePlan plan : activePlans) {
                InternalResolveResult planResult = resolveInternal(
                        priceList,
                        unitPublicId,
                        plan,
                        request.checkIn(),
                        request.checkOut(),
                        request.guests(),
                        request.bookingDate()
                );
                projections.add(new PlanResolveProjection(plan, planResult));
            }

            Optional<PlanResolveProjection> bestPlan = projections.stream()
                    .filter(p -> p.result.available())
                    .min(Comparator.comparing(p -> p.result.total()));

            Optional<PlanResolveProjection> defaultPlan = projections.stream()
                    .filter(p -> p.plan.isDefault())
                    .findFirst()
                    .or(() -> bestPlan);

            if (bestPlan.isEmpty()) {
                String reason = projections.stream()
                        .map(p -> p.result.unavailableReason())
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(UNAVAILABLE_NO_PRICE);
                items.add(BestRateItemDto.builder()
                        .unitPublicId(unitPublicId)
                        .available(false)
                        .unavailableReason(reason)
                        .build());
                continue;
            }

            items.add(BestRateItemDto.builder()
                    .unitPublicId(unitPublicId)
                    .available(true)
                    .bestPlan(toPlanSummary(bestPlan.get()))
                    .defaultPlan(defaultPlan.map(this::toPlanSummary).orElse(null))
                    .build());
        }

        return items;
    }

    private InternalResolveResult resolveInternal(
            PriceList priceList,
            UUID unitPublicId,
            RatePlan ratePlan,
            LocalDate checkIn,
            LocalDate checkOut,
            int guests,
            LocalDate bookingDate
    ) {
        LocalDate lastNight = checkOut.minusDays(1);
        int nights = (int) ChronoUnit.DAYS.between(checkIn, checkOut);

        List<UnitRateDay> days = unitRateDayRepository.findByRangeWithSourceSeason(
                priceList.getPublicId(),
                unitPublicId,
                checkIn,
                lastNight
        );
        Map<LocalDate, UnitRateDay> dayMap = new HashMap<>();
        for (UnitRateDay day : days) {
            dayMap.put(day.getStayDate(), day);
        }

        UnitRateDay checkInDay = dayMap.get(checkIn);
        UnitRateDay checkOutDay = dayMap.get(lastNight);

        boolean hasAllDays = days.size() == nights;
        boolean hasNoPriceDay = days.stream().anyMatch(d -> d.getPricePerNight() == null) || !hasAllDays;
        boolean hasStopSell = days.stream().anyMatch(UnitRateDay::isStopSell);

        boolean minStayMet = ratePlan.getMinStay() == null || nights >= ratePlan.getMinStay();
        boolean closedToArrival = checkInDay != null && checkInDay.isClosedToArrival();
        boolean closedToDeparture = checkOutDay != null && checkOutDay.isClosedToDeparture();

        RestrictionCheckDto restrictions = RestrictionCheckDto.builder()
                .minStayMet(minStayMet)
                .closedToArrivalOnCheckIn(closedToArrival)
                .closedToDepartureOnCheckOut(closedToDeparture)
                .requiredMinStay(!minStayMet ? ratePlan.getMinStay() : null)
                .build();

        boolean available = !hasNoPriceDay && !hasStopSell && minStayMet && !closedToArrival && !closedToDeparture;
        if (!available) {
            return InternalResolveResult.unavailable(restrictions, detectUnavailableReason(hasNoPriceDay, hasStopSell, closedToArrival, minStayMet));
        }

        List<NightBreakdownDto> nightsBreakdown = new ArrayList<>();
        BigDecimal subtotalBeforeRules = BigDecimal.ZERO;
        for (LocalDate date = checkIn; !date.isAfter(lastNight); date = date.plusDays(1)) {
            UnitRateDay day = dayMap.get(date);
            BigDecimal basePrice = scaleMoney(day.getPricePerNight());
            BigDecimal planPrice = scaleMoney(ratePlan.applyTo(basePrice));
            nightsBreakdown.add(NightBreakdownDto.builder().date(date).basePrice(basePrice).planPrice(planPrice).build());
            subtotalBeforeRules = subtotalBeforeRules.add(planPrice);
        }
        subtotalBeforeRules = scaleMoney(subtotalBeforeRules);

        int daysUntilCheckIn = (int) ChronoUnit.DAYS.between(bookingDate, checkIn);
        RateResolutionContext baseContext = new RateResolutionContext(checkIn, checkOut, null, nights, daysUntilCheckIn, guests);

        List<AppliedRuleDto> appliedRules = new ArrayList<>();
        BigDecimal running = subtotalBeforeRules;
        boolean stopChain = false;

        List<PricingRule> rules = pricingRuleRepository.findActiveForRatePlan(priceList.getPublicId(), ratePlan.getPublicId()).stream()
                .filter(rule -> !(rule instanceof MinStayOverrideRule))
                .toList();

        for (PricingRule rule : rules) {
            if (stopChain) {
                break;
            }

            BigDecimal impact = BigDecimal.ZERO;
            if (rule instanceof DayOfWeekRule) {
                for (NightBreakdownDto night : nightsBreakdown) {
                    RateResolutionContext nightCtx = baseContext.forNight(night.date());
                    if (rule.appliesTo(nightCtx)) {
                        impact = impact.add(computeImpact(night.planPrice(), rule.getAdjustmentType(), rule.getAdjustmentValue()));
                    }
                }
            } else {
                RateResolutionContext genericCtx = baseContext.forNight(checkIn);
                if (rule.appliesTo(genericCtx)) {
                    impact = computeImpact(running, rule.getAdjustmentType(), rule.getAdjustmentValue());
                }
            }

            impact = scaleMoney(impact);
            if (impact.compareTo(BigDecimal.ZERO) != 0) {
                running = scaleMoney(running.add(impact));
                appliedRules.add(AppliedRuleDto.builder()
                        .ruleName(rule.getName())
                        .ruleType(resolveRuleType(rule))
                        .adjustmentType(rule.getAdjustmentType().name())
                        .adjustmentValue(scaleMoney(rule.getAdjustmentValue()))
                        .impact(impact)
                        .build());
                if (!rule.isStackable()) {
                    stopChain = true;
                }
            }
        }

        BigDecimal subtotalAfterRules = running;

        List<Promotion> promotions = promotionRepository.findApplicable(
                bookingDate,
                checkIn,
                checkOut,
                priceList.getPublicId()
        ).stream()
                .filter(promotion -> promotion.isApplicable(bookingDate, checkIn, checkOut, nights))
                .toList();

        List<AppliedPromotionDto> appliedPromotions = new ArrayList<>();
        BigDecimal total = subtotalAfterRules;

        promotions.stream()
                .max(Comparator.comparing(p -> p.calculateDiscount(subtotalAfterRules)))
                .ifPresent(bestPromotion -> {
                    BigDecimal discount = scaleMoney(bestPromotion.calculateDiscount(subtotalAfterRules));
                    appliedPromotions.add(AppliedPromotionDto.builder()
                            .promotionName(bestPromotion.getName())
                            .discountType(bestPromotion.getDiscountType().name())
                            .discountValue(scaleMoney(bestPromotion.getDiscountValue()))
                            .impact(discount.negate())
                            .build());
                });

        if (!appliedPromotions.isEmpty()) {
            BigDecimal totalDiscount = appliedPromotions.stream()
                    .map(AppliedPromotionDto::impact)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .abs();
            total = scaleMoney(subtotalAfterRules.subtract(totalDiscount));
        }

        return InternalResolveResult.available(
                restrictions,
                nights,
                nightsBreakdown,
                subtotalBeforeRules,
                appliedRules,
                subtotalAfterRules,
                appliedPromotions,
                total
        );
    }

    private RateResolveResponseDto toResolveResponse(PriceList priceList, InternalResolveResult result) {
        if (!result.available()) {
            return RateResolveResponseDto.builder()
                    .available(false)
                    .restrictions(result.restrictions())
                    .build();
        }

        return RateResolveResponseDto.builder()
                .available(true)
                .restrictions(result.restrictions())
                .currency(priceList.getCurrency())
                .nights(result.nights())
                .nightsBreakdown(result.nightsBreakdown())
                .subtotalBeforeRules(result.subtotalBeforeRules())
                .rulesApplied(result.rulesApplied())
                .subtotalAfterRules(result.subtotalAfterRules())
                .promotionsApplied(result.promotionsApplied())
                .total(result.total())
                .build();
    }

    private RatePlan resolveRatePlanForAvailability(UUID priceListPublicId, UUID ratePlanPublicId) {
        if (ratePlanPublicId != null) {
            return findActiveRatePlan(priceListPublicId, ratePlanPublicId);
        }

        return ratePlanRepository.findByPriceListPublicIdAndIsDefaultTrueAndStatusNot(priceListPublicId, RatePlanStatus.DELETED)
                .filter(plan -> plan.getStatus() == RatePlanStatus.ACTIVE)
                .orElse(null);
    }

    private PriceList findActivePriceList(UUID priceListPublicId) {
        return priceListRepository.findByPublicId(priceListPublicId)
                .filter(priceList -> priceList.getStatus() != PriceListStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException(PriceListErrorCodes.PRICELIST_NOT_FOUND, priceListPublicId));
    }

    private RatePlan findActiveRatePlan(UUID priceListPublicId, UUID ratePlanPublicId) {
        return ratePlanRepository.findByPublicIdAndPriceListPublicId(ratePlanPublicId, priceListPublicId)
                .filter(ratePlan -> ratePlan.getStatus() != RatePlanStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException(PricingEngineErrorCodes.RESOLVE_PLAN_NOT_FOUND, ratePlanPublicId));
    }

    private String detectUnavailableReason(
            boolean hasNoPrice,
            boolean hasStopSell,
            boolean closedToArrival,
            boolean minStayMet
    ) {
        if (hasNoPrice) {
            return UNAVAILABLE_NO_PRICE;
        }
        if (hasStopSell) {
            return UNAVAILABLE_STOP_SELL;
        }
        if (closedToArrival) {
            return UNAVAILABLE_CLOSED_TO_ARRIVAL;
        }
        if (!minStayMet) {
            return UNAVAILABLE_MIN_STAY;
        }
        return UNAVAILABLE_CLOSED_TO_ARRIVAL;
    }

    private BigDecimal computeImpact(BigDecimal base, AdjustmentType type, BigDecimal value) {
        if (base == null || type == null || value == null) {
            return BigDecimal.ZERO;
        }

        return switch (type) {
            case PERCENTAGE -> base.multiply(value).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
            case FLAT -> value;
        };
    }

    private String resolveRuleType(PricingRule rule) {
        if (rule instanceof LengthOfStayRule) {
            return "LOS";
        }
        if (rule instanceof LeadTimeRule) {
            return "LEAD_TIME";
        }
        if (rule instanceof DayOfWeekRule) {
            return "DAY_OF_WEEK";
        }
        if (rule instanceof OccupancyRule) {
            return "OCCUPANCY";
        }
        return rule.getClass().getSimpleName().toUpperCase(Locale.ROOT);
    }

    private PlanSummaryDto toPlanSummary(PlanResolveProjection projection) {
        return PlanSummaryDto.builder()
                .ratePlanPublicId(projection.plan.getPublicId())
                .ratePlanName(projection.plan.getName())
                .mealPlan(projection.plan.getMealPlan() != null ? projection.plan.getMealPlan().name() : null)
                .total(projection.result.total())
                .build();
    }

    private BigDecimal scaleMoney(BigDecimal value) {
        return value == null ? null : value.setScale(2, RoundingMode.HALF_UP);
    }

    private record PlanResolveProjection(RatePlan plan, InternalResolveResult result) {
    }

    private record InternalResolveResult(
            boolean available,
            RestrictionCheckDto restrictions,
            String unavailableReason,
            int nights,
            List<NightBreakdownDto> nightsBreakdown,
            BigDecimal subtotalBeforeRules,
            List<AppliedRuleDto> rulesApplied,
            BigDecimal subtotalAfterRules,
            List<AppliedPromotionDto> promotionsApplied,
            BigDecimal total
    ) {

        private static InternalResolveResult unavailable(RestrictionCheckDto restrictions, String reason) {
            return new InternalResolveResult(false, restrictions, reason, 0, List.of(), null, List.of(), null, List.of(), null);
        }

        private static InternalResolveResult available(
                RestrictionCheckDto restrictions,
                int nights,
                List<NightBreakdownDto> nightsBreakdown,
                BigDecimal subtotalBeforeRules,
                List<AppliedRuleDto> rulesApplied,
                BigDecimal subtotalAfterRules,
                List<AppliedPromotionDto> promotionsApplied,
                BigDecimal total
        ) {
            return new InternalResolveResult(
                    true,
                    restrictions,
                    null,
                    nights,
                    nightsBreakdown,
                    subtotalBeforeRules,
                    rulesApplied,
                    subtotalAfterRules,
                    promotionsApplied,
                    total
            );
        }
    }
}

