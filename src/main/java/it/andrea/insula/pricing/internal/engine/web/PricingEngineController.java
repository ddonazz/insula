package it.andrea.insula.pricing.internal.engine.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.andrea.insula.pricing.internal.engine.dto.request.AvailabilityQueryDto;
import it.andrea.insula.pricing.internal.engine.dto.request.BestRateRequestDto;
import it.andrea.insula.pricing.internal.engine.dto.request.RateResolveRequestDto;
import it.andrea.insula.pricing.internal.engine.dto.response.AvailabilityDayDto;
import it.andrea.insula.pricing.internal.engine.dto.response.BestRateItemDto;
import it.andrea.insula.pricing.internal.engine.dto.response.RateResolveResponseDto;
import it.andrea.insula.pricing.internal.engine.service.PricingEngineService;
import it.andrea.insula.security.PermissionAuthority;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rates")
@Tag(name = "Pricing Engine", description = "Pricing calculation and availability")
public class PricingEngineController {

    private final PricingEngineService service;

    @Operation(summary = "Get daily availability and price for a unit")
    @GetMapping("/availability")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.RATE_READ + "')")
    public ResponseEntity<List<AvailabilityDayDto>> getAvailability(@Validated @ParameterObject AvailabilityQueryDto query) {
        return ResponseEntity.ok(service.getAvailability(query));
    }

    @Operation(summary = "Resolve final price breakdown for a specific stay")
    @PostMapping("/resolve")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.RATE_READ + "')")
    public ResponseEntity<RateResolveResponseDto> resolve(@Validated @RequestBody RateResolveRequestDto request) {
        return ResponseEntity.ok(service.resolve(request));
    }

    @Operation(summary = "Get best available rate across multiple units")
    @PostMapping("/best")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.RATE_READ + "')")
    public ResponseEntity<List<BestRateItemDto>> best(@Validated @RequestBody BestRateRequestDto request) {
        return ResponseEntity.ok(service.best(request));
    }
}

