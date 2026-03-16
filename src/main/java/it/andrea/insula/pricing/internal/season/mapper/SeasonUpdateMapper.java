package it.andrea.insula.pricing.internal.season.mapper;

import it.andrea.insula.pricing.internal.season.dto.request.SeasonUpdateDto;
import it.andrea.insula.pricing.internal.season.model.SeasonPeriod;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class SeasonUpdateMapper implements BiFunction<SeasonUpdateDto, SeasonPeriod, SeasonPeriod> {

    @Override
    public SeasonPeriod apply(SeasonUpdateDto dto, SeasonPeriod entity) {
        entity.setName(dto.name());
        entity.setSeasonType(dto.seasonType());
        entity.setStartDate(dto.startDate());
        entity.setEndDate(dto.endDate());
        entity.setPriority(dto.priority());
        entity.setStatus(dto.status());
        return entity;
    }
}

