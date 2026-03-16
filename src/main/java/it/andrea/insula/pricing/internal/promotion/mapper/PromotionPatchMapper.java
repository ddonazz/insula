package it.andrea.insula.pricing.internal.promotion.mapper;

import it.andrea.insula.pricing.internal.promotion.dto.request.PromotionPatchDto;
import it.andrea.insula.pricing.internal.promotion.model.Promotion;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class PromotionPatchMapper implements BiFunction<PromotionPatchDto, Promotion, Promotion> {

    @Override
    public Promotion apply(PromotionPatchDto dto, Promotion entity) {
        if (dto.name() != null) entity.setName(dto.name());
        if (dto.description() != null) entity.setDescription(dto.description());
        if (dto.bookingFrom() != null) entity.setBookingFrom(dto.bookingFrom());
        if (dto.bookingTo() != null) entity.setBookingTo(dto.bookingTo());
        if (dto.stayFrom() != null) entity.setStayFrom(dto.stayFrom());
        if (dto.stayTo() != null) entity.setStayTo(dto.stayTo());
        if (dto.minNights() != null) entity.setMinNights(dto.minNights());
        if (dto.discountType() != null) entity.setDiscountType(dto.discountType());
        if (dto.discountValue() != null) entity.setDiscountValue(dto.discountValue());
        if (dto.maxUsages() != null) entity.setMaxUsages(dto.maxUsages());
        if (dto.status() != null) entity.setStatus(dto.status());
        return entity;
    }
}

