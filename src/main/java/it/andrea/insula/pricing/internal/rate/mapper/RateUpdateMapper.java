package it.andrea.insula.pricing.internal.rate.mapper;

import it.andrea.insula.pricing.internal.rate.dto.request.RateUpdateDto;
import it.andrea.insula.pricing.internal.rate.model.UnitRateDay;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class RateUpdateMapper implements BiFunction<RateUpdateDto, UnitRateDay, UnitRateDay> {

    @Override
    public UnitRateDay apply(RateUpdateDto dto, UnitRateDay rate) {
        rate.setUnitPublicId(dto.unitPublicId());
        rate.setStayDate(dto.stayDate());
        rate.setPricePerNight(dto.pricePerNight());
        rate.setExtraGuestPrice(dto.extraGuestPrice());
        rate.setMinStay(dto.minStay());
        rate.setMaxStay(dto.maxStay());
        rate.setStopSell(dto.stopSell());
        rate.setClosedToArrival(dto.closedToArrival());
        rate.setClosedToDeparture(dto.closedToDeparture());
        return rate;
    }
}

