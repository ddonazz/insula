package it.andrea.insula.property.internal.unit.mapper;

import it.andrea.insula.property.internal.unit.dto.request.UnitCreateDto;
import it.andrea.insula.property.internal.unit.model.CadastralData;
import it.andrea.insula.property.internal.unit.model.Unit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UnitCreateMapper implements Function<UnitCreateDto, Unit> {

    private final CadastralDataCreateMapper cadastralDataCreateMapper;
    private final EnergyCertificateCreateMapper energyCertificateCreateMapper;

    @Override
    public Unit apply(UnitCreateDto dto) {
        Unit unit = new Unit();
        unit.setInternalName(dto.internalName());
        unit.setType(dto.type());
        unit.setFloor(dto.floor());
        unit.setInternalNumber(dto.internalNumber());
        unit.setTotalAreaMq(dto.totalAreaMq());
        unit.setWalkableAreaMq(dto.walkableAreaMq());
        unit.setRoomCount(dto.roomCount());
        unit.setBedroomCount(dto.bedroomCount());
        unit.setBathroomCount(dto.bathroomCount());
        unit.setMaxOccupancy(dto.maxOccupancy());
        unit.setRegionalIdentifierCode(dto.regionalIdentifierCode());

        if (dto.energyCertificate() != null) {
            var cert = energyCertificateCreateMapper.apply(dto.energyCertificate());
            cert.setUnit(unit);
            unit.setEnergyCertificate(cert);
        }

        if (dto.cadastralData() != null && !dto.cadastralData().isEmpty()) {
            Set<CadastralData> cadastralDataSet = dto.cadastralData().stream()
                    .map(cdDto -> {
                        CadastralData cd = cadastralDataCreateMapper.apply(cdDto);
                        cd.setUnit(unit);
                        return cd;
                    })
                    .collect(Collectors.toSet());
            unit.setCadastralData(cadastralDataSet);
        }

        if (dto.amenities() != null) {
            unit.setAmenities(new HashSet<>(dto.amenities()));
        }

        return unit;
    }
}

