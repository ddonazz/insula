package it.andrea.insula.property.internal.unit.mapper;

import it.andrea.insula.core.dto.EnumTranslator;
import it.andrea.insula.property.internal.unit.dto.response.UnitResponseDto;
import it.andrea.insula.property.internal.unit.model.Unit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UnitResponseMapper implements Function<Unit, UnitResponseDto> {

    private final EnumTranslator enumTranslator;
    private final CadastralDataResponseMapper cadastralDataResponseMapper;
    private final EnergyCertificateResponseMapper energyCertificateResponseMapper;

    @Override
    public UnitResponseDto apply(Unit unit) {
        return UnitResponseDto.builder()
                .publicId(unit.getPublicId())
                .propertyPublicId(unit.getProperty() != null ? unit.getProperty().getPublicId() : null)
                .internalName(unit.getInternalName())
                .type(enumTranslator.translate(unit.getType()))
                .floor(unit.getFloor())
                .internalNumber(unit.getInternalNumber())
                .totalAreaMq(unit.getTotalAreaMq())
                .walkableAreaMq(unit.getWalkableAreaMq())
                .roomCount(unit.getRoomCount())
                .bedroomCount(unit.getBedroomCount())
                .bathroomCount(unit.getBathroomCount())
                .maxOccupancy(unit.getMaxOccupancy())
                .regionalIdentifierCode(unit.getRegionalIdentifierCode())
                .energyCertificate(energyCertificateResponseMapper.apply(unit.getEnergyCertificate()))
                .cadastralData(unit.getCadastralData() != null
                        ? unit.getCadastralData().stream().map(cadastralDataResponseMapper).collect(Collectors.toSet())
                        : Collections.emptySet())
                .amenities(enumTranslator.translateAll(unit.getAmenities()))
                .build();
    }
}
