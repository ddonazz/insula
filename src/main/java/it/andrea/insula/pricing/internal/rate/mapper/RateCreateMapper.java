package it.andrea.insula.pricing.internal.rate.mapper;

import it.andrea.insula.pricing.internal.rate.dto.request.RateCreateDto;
import it.andrea.insula.pricing.internal.rate.model.UnitRateDay;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class RateCreateMapper implements Function<RateCreateDto, UnitRateDay> {

    @Override
    public UnitRateDay apply(RateCreateDto dto) {
        UnitRateDay rate = new UnitRateDay();
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

