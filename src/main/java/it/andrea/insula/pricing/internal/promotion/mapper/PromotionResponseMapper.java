package it.andrea.insula.pricing.internal.promotion.mapper;

import it.andrea.insula.core.dto.EnumTranslator;
import it.andrea.insula.pricing.internal.promotion.dto.response.PromotionResponseDto;
import it.andrea.insula.pricing.internal.promotion.model.Promotion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class PromotionResponseMapper implements Function<Promotion, PromotionResponseDto> {

    private final EnumTranslator enumTranslator;

    @Override
    public PromotionResponseDto apply(Promotion entity) {
        return PromotionResponseDto.builder()
                .publicId(entity.getPublicId())
                .priceListPublicId(entity.getPriceList() != null ? entity.getPriceList().getPublicId() : null)
                .priceListName(entity.getPriceList() != null ? entity.getPriceList().getName() : null)
                .name(entity.getName())
                .description(entity.getDescription())
                .bookingFrom(entity.getBookingFrom())
                .bookingTo(entity.getBookingTo())
                .stayFrom(entity.getStayFrom())
                .stayTo(entity.getStayTo())
                .minNights(entity.getMinNights())
                .discountType(enumTranslator.translate(entity.getDiscountType()))
                .discountValue(entity.getDiscountValue())
                .maxUsages(entity.getMaxUsages())
                .currentUsages(entity.getCurrentUsages())
                .status(enumTranslator.translate(entity.getStatus()))
                .build();
    }
}

