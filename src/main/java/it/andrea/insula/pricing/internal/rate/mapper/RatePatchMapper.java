package it.andrea.insula.pricing.internal.rate.mapper;

import it.andrea.insula.pricing.internal.rate.dto.request.RatePatchDto;
import it.andrea.insula.pricing.internal.rate.model.UnitRateDay;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class RatePatchMapper implements BiFunction<RatePatchDto, UnitRateDay, UnitRateDay> {

    @Override
    public UnitRateDay apply(RatePatchDto dto, UnitRateDay rate) {
        if (dto.unitPublicId() != null) {
            rate.setUnitPublicId(dto.unitPublicId());
        }
        if (dto.stayDate() != null) {
            rate.setStayDate(dto.stayDate());
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
        return rate;
    }
}

