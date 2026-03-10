package it.andrea.insula.property.internal.room.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByPublicId(UUID publicId);

    Optional<Room> findByPublicIdAndUnitPublicId(UUID publicId, UUID unitPublicId);

    List<Room> findAllByUnitPublicId(UUID unitPublicId);
}

