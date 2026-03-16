package it.andrea.insula.pricing.internal.season.mapper;

import it.andrea.insula.pricing.internal.season.dto.request.SeasonPatchDto;
import it.andrea.insula.pricing.internal.season.model.SeasonPeriod;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class SeasonPatchMapper implements BiFunction<SeasonPatchDto, SeasonPeriod, SeasonPeriod> {

    @Override
    public SeasonPeriod apply(SeasonPatchDto dto, SeasonPeriod entity) {
        if (dto.name() != null) entity.setName(dto.name());
        if (dto.seasonType() != null) entity.setSeasonType(dto.seasonType());
        if (dto.startDate() != null) entity.setStartDate(dto.startDate());
        if (dto.endDate() != null) entity.setEndDate(dto.endDate());
        if (dto.priority() != null) entity.setPriority(dto.priority());
        if (dto.status() != null) entity.setStatus(dto.status());
        return entity;
    }
}

