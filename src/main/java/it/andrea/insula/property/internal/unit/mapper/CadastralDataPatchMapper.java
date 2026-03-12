package it.andrea.insula.property.internal.unit.mapper;

import it.andrea.insula.property.internal.unit.dto.request.CadastralDataPatchDto;
import it.andrea.insula.property.internal.unit.model.CadastralData;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class CadastralDataPatchMapper implements BiFunction<CadastralDataPatchDto, CadastralData, CadastralData> {

    @Override
    public CadastralData apply(CadastralDataPatchDto dto, CadastralData entity) {
        if (dto.sheet() != null) {
            entity.setSheet(dto.sheet());
        }
        if (dto.parcel() != null) {
            entity.setParcel(dto.parcel());
        }
        if (dto.subordinate() != null) {
            entity.setSubordinate(dto.subordinate());
        }
        if (dto.category() != null) {
            entity.setCategory(dto.category());
        }
        if (dto.buildingClass() != null) {
            entity.setBuildingClass(dto.buildingClass());
        }
        if (dto.consistency() != null) {
            entity.setConsistency(dto.consistency());
        }
        if (dto.cadastralIncome() != null) {
            entity.setCadastralIncome(dto.cadastralIncome());
        }
        return entity;
    }
}

