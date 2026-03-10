package it.andrea.insula.property.internal.unit.mapper;

import it.andrea.insula.property.internal.unit.dto.request.CadastralDataCreateDto;
import it.andrea.insula.property.internal.unit.model.CadastralData;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CadastralDataCreateMapper implements Function<CadastralDataCreateDto, CadastralData> {

    @Override
    public CadastralData apply(CadastralDataCreateDto dto) {
        CadastralData data = new CadastralData();
        data.setSheet(dto.sheet());
        data.setParcel(dto.parcel());
        data.setSubordinate(dto.subordinate());
        data.setCategory(dto.category());
        data.setBuildingClass(dto.buildingClass());
        data.setConsistency(dto.consistency());
        data.setCadastralIncome(dto.cadastralIncome());
        return data;
    }
}

