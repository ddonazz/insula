package it.andrea.insula.pricing.internal.season.mapper;

import it.andrea.insula.pricing.internal.season.dto.request.SeasonCreateDto;
import it.andrea.insula.pricing.internal.season.model.SeasonPeriod;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class SeasonCreateMapper implements Function<SeasonCreateDto, SeasonPeriod> {

    @Override
    public SeasonPeriod apply(SeasonCreateDto dto) {
        SeasonPeriod entity = new SeasonPeriod();
        entity.setName(dto.name());
        entity.setSeasonType(dto.seasonType());
        entity.setStartDate(dto.startDate());
        entity.setEndDate(dto.endDate());
        entity.setPriority(dto.priority());
        return entity;
    }
}

