package it.andrea.insula.property.internal.property.exception;

import it.andrea.insula.core.exception.ErrorDefinition;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PropertyErrorCodes implements ErrorDefinition {

    PROPERTY_NOT_FOUND(40001, "property.not.found", "Property not found with the provided identifier."),
    PROPERTY_NAME_IN_USE(40002, "property.name.inuse", "The property name is already in use within this agency."),
    UNIT_NOT_FOUND(40003, "unit.not.found", "Unit not found with the provided identifier."),
    UNIT_RIC_IN_USE(40004, "unit.ric.inuse", "The regional identifier code is already in use by another unit."),
    ROOM_NOT_FOUND(40005, "room.not.found", "Room not found with the provided identifier.");

    private final int code;
    private final String errorCode;
    private final String defaultMessage;
}

