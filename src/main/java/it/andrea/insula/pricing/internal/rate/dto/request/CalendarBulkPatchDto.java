package it.andrea.insula.pricing.internal.rate.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
public record CalendarBulkPatchDto(
        @NotEmpty
        List<UUID> unitPublicIds,

        @NotNull
        LocalDate from,

        @NotNull
        LocalDate after,

        @NotNull
        CalendarDayPatchDto patch
) {
}

