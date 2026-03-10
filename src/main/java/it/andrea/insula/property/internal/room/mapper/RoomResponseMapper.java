package it.andrea.insula.property.internal.room.mapper;

import it.andrea.insula.core.dto.EnumTranslator;
import it.andrea.insula.property.internal.room.dto.response.RoomResponseDto;
import it.andrea.insula.property.internal.room.model.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class RoomResponseMapper implements Function<Room, RoomResponseDto> {

    private final EnumTranslator enumTranslator;

    @Override
    public RoomResponseDto apply(Room room) {
        return RoomResponseDto.builder()
                .publicId(room.getPublicId())
                .unitPublicId(room.getUnit() != null ? room.getUnit().getPublicId() : null)
                .type(enumTranslator.translate(room.getType()))
                .areaMq(room.getAreaMq())
                .features(room.getFeatures() != null ? room.getFeatures() : Collections.emptySet())
                .build();
    }
}
