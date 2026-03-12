package it.andrea.insula.pricing.internal.rate.mapper;

import it.andrea.insula.pricing.internal.rate.dto.request.RateCreateDto;
import it.andrea.insula.pricing.internal.rate.model.UnitRatePeriod;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.function.Function;

@Component
public class RateCreateMapper implements Function<RateCreateDto, UnitRatePeriod> {

    @Override
    public UnitRatePeriod apply(RateCreateDto dto) {
        UnitRatePeriod rate = new UnitRatePeriod();
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

