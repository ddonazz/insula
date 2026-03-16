package it.andrea.insula.pricing.internal.rate.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.andrea.insula.pricing.internal.rate.dto.request.CalendarBulkPatchDto;
import it.andrea.insula.pricing.internal.rate.dto.request.CalendarDayPatchDto;
import it.andrea.insula.pricing.internal.rate.dto.response.CalendarDayDto;
import it.andrea.insula.pricing.internal.rate.service.RateCalendarBackofficeService;
import it.andrea.insula.security.PermissionAuthority;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/price-lists/{priceListPublicId}/calendar")
@Tag(name = "Rate Calendar", description = "Back-office calendar operations")
public class RateCalendarController {

    private final RateCalendarBackofficeService service;

    @Operation(summary = "Get daily calendar for one unit")
    @GetMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.RATE_READ + "')")
    public ResponseEntity<List<CalendarDayDto>> getCalendar(
            @PathVariable UUID priceListPublicId,
            @RequestParam UUID unitPublicId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(service.getCalendar(priceListPublicId, unitPublicId, from, to));
    }

    @Operation(summary = "Patch calendar in bulk")
    @PatchMapping("/bulk")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.RATE_UPDATE + "')")
    public ResponseEntity<Map<String, Integer>> bulkPatch(
            @PathVariable UUID priceListPublicId,
            @Validated @RequestBody CalendarBulkPatchDto dto
    ) {
        int updated = service.bulkPatch(priceListPublicId, dto);
        return ResponseEntity.ok(Map.of("updated", updated));
    }

    @Operation(summary = "Patch one calendar day")
    @PatchMapping("/{date}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.RATE_UPDATE + "')")
    public ResponseEntity<CalendarDayDto> patchDay(
            @PathVariable UUID priceListPublicId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam UUID unitPublicId,
            @Validated @RequestBody CalendarDayPatchDto dto
    ) {
        return ResponseEntity.ok(service.patchDay(priceListPublicId, unitPublicId, date, dto));
    }
}

