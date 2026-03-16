package it.andrea.insula.pricing.internal.rateplan.mapper;

import it.andrea.insula.core.dto.EnumTranslator;
import it.andrea.insula.pricing.internal.rateplan.dto.response.RatePlanResponseDto;
import it.andrea.insula.pricing.internal.rateplan.model.RatePlan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class RatePlanResponseMapper implements Function<RatePlan, RatePlanResponseDto> {

    private final EnumTranslator enumTranslator;

    @Override
    public RatePlanResponseDto apply(RatePlan entity) {
        return RatePlanResponseDto.builder()
                .publicId(entity.getPublicId())
                .priceListPublicId(entity.getPriceList() != null ? entity.getPriceList().getPublicId() : null)
                .priceListName(entity.getPriceList() != null ? entity.getPriceList().getName() : null)
                .name(entity.getName())
                .description(entity.getDescription())
                .mealPlan(enumTranslator.translate(entity.getMealPlan()))
                .adjustmentType(enumTranslator.translate(entity.getAdjustmentType()))
                .adjustmentValue(entity.getAdjustmentValue())
                .minStay(entity.getMinStay())
                .maxStay(entity.getMaxStay())
                .closedToArrival(entity.isClosedToArrival())
                .closedToDeparture(entity.isClosedToDeparture())
                .isDefault(entity.isDefault())
                .requiresPromoCode(entity.isRequiresPromoCode())
                .status(enumTranslator.translate(entity.getStatus()))
                .build();
    }
}

