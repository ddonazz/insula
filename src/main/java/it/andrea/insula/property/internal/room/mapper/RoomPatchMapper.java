package it.andrea.insula.property.internal.room.mapper;

import it.andrea.insula.property.internal.room.dto.request.RoomPatchDto;
import it.andrea.insula.property.internal.room.model.Room;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.function.BiFunction;

@Component
public class RoomPatchMapper implements BiFunction<RoomPatchDto, Room, Room> {

    @Override
    public Room apply(RoomPatchDto dto, Room room) {
        if (dto.type() != null) {
            room.setType(dto.type());
        }
        if (dto.areaMq() != null) {
            room.setAreaMq(dto.areaMq());
        }
        if (dto.features() != null) {
            room.setFeatures(new HashSet<>(dto.features()));
        }
        return room;
    }
}

