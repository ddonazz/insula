package it.andrea.insula.pricing.internal.rateplan.mapper;

import it.andrea.insula.pricing.internal.rateplan.dto.request.RatePlanPatchDto;
import it.andrea.insula.pricing.internal.rateplan.model.RatePlan;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class RatePlanPatchMapper implements BiFunction<RatePlanPatchDto, RatePlan, RatePlan> {

    @Override
    public RatePlan apply(RatePlanPatchDto dto, RatePlan entity) {
        if (dto.name() != null) entity.setName(dto.name());
        if (dto.description() != null) entity.setDescription(dto.description());
        if (dto.mealPlan() != null) entity.setMealPlan(dto.mealPlan());
        if (dto.adjustmentType() != null) entity.setAdjustmentType(dto.adjustmentType());
        if (dto.adjustmentValue() != null) entity.setAdjustmentValue(dto.adjustmentValue());
        if (dto.minStay() != null) entity.setMinStay(dto.minStay());
        if (dto.maxStay() != null) entity.setMaxStay(dto.maxStay());
        if (dto.closedToArrival() != null) entity.setClosedToArrival(dto.closedToArrival());
        if (dto.closedToDeparture() != null) entity.setClosedToDeparture(dto.closedToDeparture());
        if (dto.isDefault() != null) entity.setDefault(dto.isDefault());
        if (dto.requiresPromoCode() != null) entity.setRequiresPromoCode(dto.requiresPromoCode());
        if (dto.status() != null) entity.setStatus(dto.status());
        return entity;
    }
}

