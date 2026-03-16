package it.andrea.insula.pricing.internal.promotion.mapper;

import it.andrea.insula.pricing.internal.promotion.dto.request.PromotionUpdateDto;
import it.andrea.insula.pricing.internal.promotion.model.Promotion;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class PromotionUpdateMapper implements BiFunction<PromotionUpdateDto, Promotion, Promotion> {

    @Override
    public Promotion apply(PromotionUpdateDto dto, Promotion entity) {
        entity.setName(dto.name());
        entity.setDescription(dto.description());
        entity.setBookingFrom(dto.bookingFrom());
        entity.setBookingTo(dto.bookingTo());
        entity.setStayFrom(dto.stayFrom());
        entity.setStayTo(dto.stayTo());
        entity.setMinNights(dto.minNights());
        entity.setDiscountType(dto.discountType());
        entity.setDiscountValue(dto.discountValue());
        entity.setMaxUsages(dto.maxUsages());
        entity.setStatus(dto.status());
        return entity;
    }
}

