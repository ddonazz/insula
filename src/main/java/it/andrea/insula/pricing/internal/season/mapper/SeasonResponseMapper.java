package it.andrea.insula.pricing.internal.season.mapper;

import it.andrea.insula.core.dto.EnumTranslator;
import it.andrea.insula.pricing.internal.season.dto.response.SeasonResponseDto;
import it.andrea.insula.pricing.internal.season.model.SeasonPeriod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class SeasonResponseMapper implements Function<SeasonPeriod, SeasonResponseDto> {

    private final EnumTranslator enumTranslator;

    @Override
    public SeasonResponseDto apply(SeasonPeriod entity) {
        return SeasonResponseDto.builder()
                .publicId(entity.getPublicId())
                .priceListPublicId(entity.getPriceList() != null ? entity.getPriceList().getPublicId() : null)
                .priceListName(entity.getPriceList() != null ? entity.getPriceList().getName() : null)
                .name(entity.getName())
                .seasonType(enumTranslator.translate(entity.getSeasonType()))
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .priority(entity.getPriority())
                .status(enumTranslator.translate(entity.getStatus()))
                .build();
    }
}

