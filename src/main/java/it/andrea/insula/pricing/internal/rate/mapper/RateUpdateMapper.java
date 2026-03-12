package it.andrea.insula.pricing.internal.rate.mapper;

import it.andrea.insula.pricing.internal.rate.dto.request.RateUpdateDto;
import it.andrea.insula.pricing.internal.rate.model.UnitRatePeriod;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.function.BiFunction;

@Component
public class RateUpdateMapper implements BiFunction<RateUpdateDto, UnitRatePeriod, UnitRatePeriod> {

    @Override
    public UnitRatePeriod apply(RateUpdateDto dto, UnitRatePeriod rate) {
        rate.setUnitPublicId(dto.unitPublicId());
        rate.setStartDate(dto.startDate());
        rate.setEndDate(dto.endDate());
        rate.setPricePerNight(dto.pricePerNight());
        rate.setExtraGuestPrice(dto.extraGuestPrice());
        rate.setMinStay(dto.minStay());
        rate.setMaxStay(dto.maxStay());
        rate.setStopSell(dto.stopSell());
        rate.setClosedToArrival(dto.closedToArrival());
        rate.setClosedToDeparture(dto.closedToDeparture());
        rate.setAllowedCheckInDays(dto.allowedCheckInDays() != null ? new HashSet<>(dto.allowedCheckInDays()) : new HashSet<>());
        rate.setAllowedCheckOutDays(dto.allowedCheckOutDays() != null ? new HashSet<>(dto.allowedCheckOutDays()) : new HashSet<>());
        return rate;
    }
}

