package it.andrea.insula.pricing.internal.rule.mapper;

import it.andrea.insula.core.dto.EnumTranslator;
import it.andrea.insula.pricing.internal.rule.dto.response.PricingRuleResponseDto;
import it.andrea.insula.pricing.internal.rule.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class PricingRuleResponseMapper implements Function<PricingRule, PricingRuleResponseDto> {

    private final EnumTranslator enumTranslator;

    @Override
    public PricingRuleResponseDto apply(PricingRule entity) {
        PricingRuleResponseDto.PricingRuleResponseDtoBuilder builder = PricingRuleResponseDto.builder()
                .publicId(entity.getPublicId())
                .priceListPublicId(entity.getPriceList() != null ? entity.getPriceList().getPublicId() : null)
                .ratePlanPublicId(entity.getRatePlan() != null ? entity.getRatePlan().getPublicId() : null)
                .name(entity.getName())
                .type(resolveType(entity))
                .adjustmentType(enumTranslator.translate(entity.getAdjustmentType()))
                .adjustmentValue(entity.getAdjustmentValue())
                .priority(entity.getPriority())
                .stackable(entity.isStackable())
                .status(enumTranslator.translate(entity.getStatus()));

        if (entity instanceof LengthOfStayRule los) {
            builder.minNights(los.getMinNights()).maxNights(los.getMaxNights());
        }
        if (entity instanceof LeadTimeRule lead) {
            builder.minDaysInAdvance(lead.getMinDaysInAdvance()).maxDaysInAdvance(lead.getMaxDaysInAdvance());
        }
        if (entity instanceof DayOfWeekRule dow) {
            builder.applyOnDays(dow.getApplyOnDays());
        }
        if (entity instanceof OccupancyRule occ) {
            builder.guestsThreshold(occ.getGuestsThreshold());
        }
        if (entity instanceof MinStayOverrideRule minStay) {
            builder.applyFromDate(minStay.getApplyFromDate())
                    .applyToDate(minStay.getApplyToDate())
                    .minStayRequired(minStay.getMinStayRequired());
        }
        return builder.build();
    }

    private String resolveType(PricingRule entity) {
        if (entity instanceof LengthOfStayRule) return "LOS";
        if (entity instanceof LeadTimeRule) return "LEAD_TIME";
        if (entity instanceof DayOfWeekRule) return "DAY_OF_WEEK";
        if (entity instanceof OccupancyRule) return "OCCUPANCY";
        if (entity instanceof MinStayOverrideRule) return "MIN_STAY";
        return "UNKNOWN";
    }
}

