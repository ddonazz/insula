package it.andrea.insula.property.internal.room.mapper;

import it.andrea.insula.property.internal.room.dto.request.RoomCreateDto;
import it.andrea.insula.property.internal.room.model.Room;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.function.Function;

@Component
public class RoomCreateMapper implements Function<RoomCreateDto, Room> {

    @Override
    public Room apply(RoomCreateDto dto) {
        Room room = new Room();
        room.setType(dto.type());
        room.setAreaMq(dto.areaMq());
        if (dto.features() != null) {
            room.setFeatures(new HashSet<>(dto.features()));
        }
        return room;
    }
}

