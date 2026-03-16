package it.andrea.insula.pricing.internal.season.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.andrea.insula.pricing.internal.season.dto.request.SeasonGenerateDto;
import it.andrea.insula.pricing.internal.season.dto.response.SeasonGenerateResultDto;
import it.andrea.insula.pricing.internal.season.service.SeasonGenerationService;
import it.andrea.insula.security.PermissionAuthority;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/price-lists/{priceListPublicId}/seasons/{seasonPublicId}")
@Tag(name = "Season Management", description = "Season operations")
public class SeasonGenerationController {

    private final SeasonGenerationService service;

    @Operation(summary = "Generate daily rate records from a season period")
    @PostMapping("/generate")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.RATE_CREATE + "')")
    public ResponseEntity<SeasonGenerateResultDto> generate(
            @PathVariable UUID priceListPublicId,
            @PathVariable UUID seasonPublicId,
            @Validated @RequestBody SeasonGenerateDto dto
    ) {
        return ResponseEntity.ok(service.generate(priceListPublicId, seasonPublicId, dto));
    }
}

