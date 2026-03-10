package it.andrea.insula.property.internal.unit.mapper;

import it.andrea.insula.property.internal.unit.dto.response.CadastralDataResponseDto;
import it.andrea.insula.property.internal.unit.model.CadastralData;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CadastralDataResponseMapper implements Function<CadastralData, CadastralDataResponseDto> {

    @Override
    public CadastralDataResponseDto apply(CadastralData data) {
        if (data == null) return null;
        return CadastralDataResponseDto.builder()
                .publicId(data.getPublicId())
                .sheet(data.getSheet())
                .parcel(data.getParcel())
                .subordinate(data.getSubordinate())
                .category(data.getCategory())
                .buildingClass(data.getBuildingClass())
                .consistency(data.getConsistency())
                .cadastralIncome(data.getCadastralIncome())
                .build();
    }
}

