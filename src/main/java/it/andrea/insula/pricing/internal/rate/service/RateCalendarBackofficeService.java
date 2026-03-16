package it.andrea.insula.pricing.internal.rate.service;

import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.pricing.internal.pricelist.exception.PriceListErrorCodes;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListRepository;
import it.andrea.insula.pricing.internal.pricelist.model.PriceListStatus;
import it.andrea.insula.pricing.internal.rate.dto.request.CalendarBulkPatchDto;
import it.andrea.insula.pricing.internal.rate.dto.request.CalendarDayPatchDto;
import it.andrea.insula.pricing.internal.rate.dto.response.CalendarDayDto;
import it.andrea.insula.pricing.internal.rate.model.UnitRateDay;
import it.andrea.insula.pricing.internal.rate.model.UnitRateDayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RateCalendarBackofficeService {

    private final UnitRateDayRepository unitRateDayRepository;
    private final PriceListRepository priceListRepository;
    private final RateCalendarValidator validator;

    public List<CalendarDayDto> getCalendar(
            UUID priceListPublicId,
            UUID unitPublicId,
            LocalDate from,
            LocalDate to
    ) {
        findActivePriceList(priceListPublicId);
        validator.validateUnit(unitPublicId);
        validator.validateRange(from, to);

        List<UnitRateDay> days = unitRateDayRepository.findByRangeWithSourceSeason(
                priceListPublicId,
                unitPublicId,
                from,
                to.minusDays(1)
        );

        return days.stream().map(this::toCalendarDay).toList();
    }

    @Transactional
    public int bulkPatch(UUID priceListPublicId, CalendarBulkPatchDto dto) {
        PriceList priceList = findActivePriceList(priceListPublicId);
        validator.validateRange(dto.from(), dto.after());

        List<UUID> unitPublicIds = dto.unitPublicIds().stream().distinct().toList();
        List<UnitRateDay> existing = unitRateDayRepository.findByPriceListPublicIdAndUnitPublicIdInAndStayDateBetween(
                priceListPublicId,
                unitPublicIds,
                dto.from(),
                dto.after().minusDays(1)
        );

        Map<RateKey, UnitRateDay> existingMap = new HashMap<>();
        for (UnitRateDay day : existing) {
            existingMap.put(new RateKey(day.getUnitPublicId(), day.getStayDate()), day);
        }

        List<UnitRateDay> batch = new ArrayList<>();
        for (UUID unitPublicId : unitPublicIds) {
            for (LocalDate date = dto.from(); date.isBefore(dto.after()); date = date.plusDays(1)) {
                RateKey key = new RateKey(unitPublicId, date);
                UnitRateDay day = existingMap.get(key);
                if (day == null) {
                    day = createManualDay(priceList, unitPublicId, date);
                }
                applyPatch(day, dto.patch());
                day.setSourceSeason(null);
                batch.add(day);
            }
        }

        unitRateDayRepository.saveAll(batch);
        return batch.size();
    }

    @Transactional
    public CalendarDayDto patchDay(
            UUID priceListPublicId,
            UUID unitPublicId,
            LocalDate date,
            CalendarDayPatchDto dto
    ) {
        PriceList priceList = findActivePriceList(priceListPublicId);
        validator.validateUnit(unitPublicId);

        UnitRateDay day = unitRateDayRepository
                .findByPriceListPublicIdAndUnitPublicIdAndStayDate(priceListPublicId, unitPublicId, date)
                .orElseGet(() -> createManualDay(priceList, unitPublicId, date));

        applyPatch(day, dto);
        day.setSourceSeason(null);

        UnitRateDay saved = unitRateDayRepository.save(day);
        return toCalendarDay(saved);
    }

    private UnitRateDay createManualDay(PriceList priceList, UUID unitPublicId, LocalDate date) {
        UnitRateDay day = new UnitRateDay();
        day.setPriceList(priceList);
        day.setUnitPublicId(unitPublicId);
        day.setStayDate(date);
        day.setStopSell(false);
        day.setClosedToArrival(false);
        day.setClosedToDeparture(false);
        day.setSourceSeason(null);
        return day;
    }

    private void applyPatch(UnitRateDay day, CalendarDayPatchDto patch) {
        if (patch.pricePerNight() != null) {
            day.setPricePerNight(patch.pricePerNight());
        }
        if (patch.extraGuestPrice() != null) {
            day.setExtraGuestPrice(patch.extraGuestPrice());
        }
        if (patch.minStay() != null) {
            day.setMinStay(patch.minStay());
        }
        if (patch.maxStay() != null) {
            day.setMaxStay(patch.maxStay());
        }
        if (patch.stopSell() != null) {
            day.setStopSell(patch.stopSell());
        }
        if (patch.closedToArrival() != null) {
            day.setClosedToArrival(patch.closedToArrival());
        }
        if (patch.closedToDeparture() != null) {
            day.setClosedToDeparture(patch.closedToDeparture());
        }
    }

    private CalendarDayDto toCalendarDay(UnitRateDay day) {
        boolean fromSeason = day.getSourceSeason() != null;
        return CalendarDayDto.builder()
                .date(day.getStayDate())
                .pricePerNight(day.getPricePerNight())
                .extraGuestPrice(day.getExtraGuestPrice())
                .minStay(day.getMinStay())
                .maxStay(day.getMaxStay())
                .stopSell(day.isStopSell())
                .closedToArrival(day.isClosedToArrival())
                .closedToDeparture(day.isClosedToDeparture())
                .source(fromSeason ? "SEASON" : "MANUAL")
                .seasonName(fromSeason ? day.getSourceSeason().getName() : null)
                .build();
    }

    private PriceList findActivePriceList(UUID priceListPublicId) {
        return priceListRepository.findByPublicId(priceListPublicId)
                .filter(priceList -> priceList.getStatus() != PriceListStatus.DELETED)
                .orElseThrow(() -> new ResourceNotFoundException(PriceListErrorCodes.PRICELIST_NOT_FOUND, priceListPublicId));
    }

    private record RateKey(UUID unitPublicId, LocalDate date) {
    }
}

