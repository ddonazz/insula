package it.andrea.insula.pricing.internal.rateplan.mapper;

import it.andrea.insula.pricing.internal.rateplan.dto.request.RatePlanUpdateDto;
import it.andrea.insula.pricing.internal.rateplan.model.RatePlan;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class RatePlanUpdateMapper implements BiFunction<RatePlanUpdateDto, RatePlan, RatePlan> {

    @Override
    public RatePlan apply(RatePlanUpdateDto dto, RatePlan entity) {
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
        entity.setStatus(dto.status());
        return entity;
    }
}

