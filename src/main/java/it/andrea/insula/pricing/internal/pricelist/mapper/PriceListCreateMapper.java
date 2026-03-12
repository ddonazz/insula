package it.andrea.insula.pricing.internal.pricelist.mapper;

import it.andrea.insula.pricing.internal.pricelist.dto.request.PriceListCreateDto;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PriceListCreateMapper implements Function<PriceListCreateDto, PriceList> {

    @Override
    public PriceList apply(PriceListCreateDto dto) {
        PriceList priceList = new PriceList();
        priceList.setName(dto.name());
        priceList.setDescription(dto.description());
        priceList.setDefault(dto.isDefault());
        priceList.setCurrency(dto.currency() != null ? dto.currency().toUpperCase() : "EUR");
        priceList.setPercentageAdjustment(dto.percentageAdjustment());
        priceList.setFlatAdjustment(dto.flatAdjustment());
        return priceList;
    }
}

