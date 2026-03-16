package it.andrea.insula.pricing.internal.rateplan.mapper;

import it.andrea.insula.pricing.internal.rateplan.dto.request.RatePlanCreateDto;
import it.andrea.insula.pricing.internal.rateplan.model.RatePlan;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class RatePlanCreateMapper implements Function<RatePlanCreateDto, RatePlan> {

    @Override
    public RatePlan apply(RatePlanCreateDto dto) {
        RatePlan entity = new RatePlan();
        entity.setName(dto.name());
        entity.setDescription(dto.description());
        entity.setMealPlan(dto.mealPlan());
        entity.setAdjustmentType(dto.adjustmentType());
        entity.setAdjustmentValue(dto.adjustmentValue());
        entity.setMinStay(dto.minStay());
        entity.setMaxStay(dto.maxStay());
        entity.setClosedToArrival(dto.closedToArrival());
        entity.setClosedToDeparture(dto.closedToDeparture());
        entity.setDefault(dto.isDefault());
        entity.setRequiresPromoCode(dto.requiresPromoCode());
        return entity;
    }
}

