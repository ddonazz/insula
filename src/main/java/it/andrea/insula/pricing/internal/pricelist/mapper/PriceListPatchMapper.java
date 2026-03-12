package it.andrea.insula.pricing.internal.pricelist.mapper;

import it.andrea.insula.pricing.internal.pricelist.dto.request.PriceListPatchDto;
import it.andrea.insula.pricing.internal.pricelist.model.PriceList;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class PriceListPatchMapper implements BiFunction<PriceListPatchDto, PriceList, PriceList> {

    @Override
    public PriceList apply(PriceListPatchDto dto, PriceList priceList) {
        if (dto.name() != null) {
            priceList.setName(dto.name());
        }
        if (dto.description() != null) {
            priceList.setDescription(dto.description());
        }
        if (dto.isDefault() != null) {
            priceList.setDefault(dto.isDefault());
        }
        if (dto.currency() != null) {
            priceList.setCurrency(dto.currency().toUpperCase());
        }
        if (dto.status() != null) {
            priceList.setStatus(dto.status());
        }
        if (dto.percentageAdjustment() != null) {
            priceList.setPercentageAdjustment(dto.percentageAdjustment());
        }
        if (dto.flatAdjustment() != null) {
            priceList.setFlatAdjustment(dto.flatAdjustment());
        }
        return priceList;
    }
}

