package it.andrea.insula.property.internal.unit.mapper;

import it.andrea.insula.property.internal.unit.dto.request.UnitPatchDto;
import it.andrea.insula.property.internal.unit.model.Unit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class UnitPatchMapper implements BiFunction<UnitPatchDto, Unit, Unit> {

    private final EnergyCertificatePatchMapper energyCertificatePatchMapper;

    @Override
    public Unit apply(UnitPatchDto dto, Unit unit) {
        if (dto.internalName() != null) {
            unit.setInternalName(dto.internalName());
        }
        if (dto.type() != null) {
            unit.setType(dto.type());
        }
        if (dto.floor() != null) {
            unit.setFloor(dto.floor());
        }
        if (dto.internalNumber() != null) {
            unit.setInternalNumber(dto.internalNumber());
        }
        if (dto.totalAreaMq() != null) {
            unit.setTotalAreaMq(dto.totalAreaMq());
        }
        if (dto.walkableAreaMq() != null) {
            unit.setWalkableAreaMq(dto.walkableAreaMq());
        }
        if (dto.roomCount() != null) {
            unit.setRoomCount(dto.roomCount());
        }
        if (dto.bedroomCount() != null) {
            unit.setBedroomCount(dto.bedroomCount());
        }
        if (dto.bathroomCount() != null) {
            unit.setBathroomCount(dto.bathroomCount());
        }
        if (dto.maxOccupancy() != null) {
            unit.setMaxOccupancy(dto.maxOccupancy());
        }
        if (dto.regionalIdentifierCode() != null) {
            unit.setRegionalIdentifierCode(dto.regionalIdentifierCode());
        }
        if (dto.energyCertificate() != null && unit.getEnergyCertificate() != null) {
            energyCertificatePatchMapper.apply(dto.energyCertificate(), unit.getEnergyCertificate());
        }
        if (dto.amenities() != null) {
            unit.setAmenities(new HashSet<>(dto.amenities()));
        }
        return unit;
    }
}

