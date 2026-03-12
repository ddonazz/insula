package it.andrea.insula.pricing.internal.rate.mapper;

import it.andrea.insula.pricing.internal.rate.dto.request.RatePatchDto;
import it.andrea.insula.pricing.internal.rate.model.UnitRatePeriod;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.function.BiFunction;

@Component
public class RatePatchMapper implements BiFunction<RatePatchDto, UnitRatePeriod, UnitRatePeriod> {

    @Override
    public UnitRatePeriod apply(RatePatchDto dto, UnitRatePeriod rate) {
        if (dto.unitPublicId() != null) {
            rate.setUnitPublicId(dto.unitPublicId());
        }
        if (dto.startDate() != null) {
            rate.setStartDate(dto.startDate());
        }
        if (dto.endDate() != null) {
            rate.setEndDate(dto.endDate());
        }
        if (dto.pricePerNight() != null) {
            rate.setPricePerNight(dto.pricePerNight());
        }
        if (dto.extraGuestPrice() != null) {
            rate.setExtraGuestPrice(dto.extraGuestPrice());
        }
        if (dto.minStay() != null) {
            rate.setMinStay(dto.minStay());
        }
        if (dto.maxStay() != null) {
            rate.setMaxStay(dto.maxStay());
        }
        if (dto.stopSell() != null) {
            rate.setStopSell(dto.stopSell());
        }
        if (dto.closedToArrival() != null) {
            rate.setClosedToArrival(dto.closedToArrival());
        }
        if (dto.closedToDeparture() != null) {
            rate.setClosedToDeparture(dto.closedToDeparture());
        }
        if (dto.allowedCheckInDays() != null) {
            rate.setAllowedCheckInDays(new HashSet<>(dto.allowedCheckInDays()));
        }
        if (dto.allowedCheckOutDays() != null) {
            rate.setAllowedCheckOutDays(new HashSet<>(dto.allowedCheckOutDays()));
        }
        return rate;
    }
}

