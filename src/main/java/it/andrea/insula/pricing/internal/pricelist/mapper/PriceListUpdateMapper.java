package it.andrea.insula.pricing.internal.pricelist.mapper;

import it.andrea.insula.pricing.internal.pricelist.dto.request.PriceListUpdateDto;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class PriceListUpdateMapper implements BiFunction<PriceListUpdateDto, PriceList, PriceList> {

    @Override
    public PriceList apply(PriceListUpdateDto dto, PriceList priceList) {
        priceList.setName(dto.name());
        priceList.setDescription(dto.description());
        priceList.setDefault(dto.isDefault());
        priceList.setCurrency(dto.currency() != null ? dto.currency().toUpperCase() : priceList.getCurrency());
        priceList.setStatus(dto.status());
        priceList.setPercentageAdjustment(dto.percentageAdjustment());
        priceList.setFlatAdjustment(dto.flatAdjustment());
        return priceList;
    }
}

