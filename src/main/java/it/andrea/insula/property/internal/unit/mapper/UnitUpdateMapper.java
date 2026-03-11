package it.andrea.insula.property.internal.unit.mapper;

import it.andrea.insula.property.internal.unit.dto.request.UnitUpdateDto;
import it.andrea.insula.property.internal.unit.model.Unit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class UnitUpdateMapper implements BiFunction<UnitUpdateDto, Unit, Unit> {

    private final EnergyCertificateUpdateMapper energyCertificateUpdateMapper;

    @Override
    public Unit apply(UnitUpdateDto dto, Unit unit) {
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
        if (dto.energyCertificate() != null && unit.getEnergyCertificate() != null) {
            energyCertificateUpdateMapper.apply(dto.energyCertificate(), unit.getEnergyCertificate());
        }
        unit.setAmenities(dto.amenities() != null ? new HashSet<>(dto.amenities()) : new HashSet<>());
        return unit;
    }
}

