package it.andrea.insula.pricing.internal.engine.dto.response;

import lombok.Builder;

@Builder
public record RestrictionCheckDto(
        boolean minStayMet,
        boolean closedToArrivalOnCheckIn,
        boolean closedToDepartureOnCheckOut,
        Integer requiredMinStay
) {
}

