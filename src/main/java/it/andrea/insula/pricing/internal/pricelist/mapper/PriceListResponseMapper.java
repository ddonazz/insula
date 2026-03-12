package it.andrea.insula.pricing.internal.pricelist.mapper;

import it.andrea.insula.core.dto.EnumTranslator;
import it.andrea.insula.pricing.internal.pricelist.dto.response.PriceListResponseDto;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class PriceListResponseMapper implements Function<PriceList, PriceListResponseDto> {

    private final EnumTranslator enumTranslator;

    @Override
    public PriceListResponseDto apply(PriceList priceList) {
        return PriceListResponseDto.builder()
                .publicId(priceList.getPublicId())
                .name(priceList.getName())
                .description(priceList.getDescription())
                .isDefault(priceList.isDefault())
                .currency(priceList.getCurrency())
                .status(enumTranslator.translate(priceList.getStatus()))
                .parentPriceListPublicId(
                        priceList.getParentPriceList() != null ? priceList.getParentPriceList().getPublicId() : null
                )
                .parentPriceListName(
                        priceList.getParentPriceList() != null ? priceList.getParentPriceList().getName() : null
                )
                .percentageAdjustment(priceList.getPercentageAdjustment())
                .flatAdjustment(priceList.getFlatAdjustment())
                .build();
    }
}

