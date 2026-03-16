package it.andrea.insula.pricing.internal.promotion.mapper;

import it.andrea.insula.pricing.internal.promotion.dto.request.PromotionCreateDto;
import it.andrea.insula.pricing.internal.promotion.model.Promotion;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PromotionCreateMapper implements Function<PromotionCreateDto, Promotion> {

    @Override
    public Promotion apply(PromotionCreateDto dto) {
        Promotion entity = new Promotion();
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
        return entity;
    }
}

