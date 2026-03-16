package it.andrea.insula.pricing.internal.rule.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.pricing.internal.rule.dto.request.*;
import it.andrea.insula.pricing.internal.rule.dto.response.PricingRuleResponseDto;
import it.andrea.insula.pricing.internal.rule.service.PricingRuleService;
import it.andrea.insula.security.PermissionAuthority;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pricing-rules")
@RequiredArgsConstructor
@Tag(name = "Pricing Rules", description = "Manage pricing rules")
public class PricingRuleController {

    private final PricingRuleService service;

    @Operation(summary = "Get pricing rule by public ID")
    @GetMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.PRICING_RULE_READ + "')")
    public ResponseEntity<PricingRuleResponseDto> getByPublicId(@PathVariable UUID publicId) {
        return ResponseEntity.ok(service.getByPublicId(publicId));
    }

    @Operation(summary = "Get pricing rules (paged)")
    @GetMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.PRICING_RULE_READ + "')")
    public ResponseEntity<PageResponse<PricingRuleResponseDto>> getAll(
            @ParameterObject PricingRuleSearchCriteria criteria,
            @ParameterObject @PageableDefault(size = 20, sort = "priority") Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(criteria, pageable));
    }

    @Operation(summary = "Get pricing rules list")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.PRICING_RULE_READ + "')")
    public ResponseEntity<List<PricingRuleResponseDto>> getList(@ParameterObject PricingRuleSearchCriteria criteria) {
        return ResponseEntity.ok(service.findAll(criteria));
    }

    @Operation(summary = "Create pricing rule")
    @PostMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.PRICING_RULE_CREATE + "')")
    public ResponseEntity<PricingRuleResponseDto> create(@Validated @RequestBody PricingRuleCreateDto dto) {
        PricingRuleResponseDto created = service.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{publicId}")
                .buildAndExpand(created.publicId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Update pricing rule")
    @PutMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.PRICING_RULE_UPDATE + "')")
    public ResponseEntity<PricingRuleResponseDto> update(@PathVariable UUID publicId, @Validated @RequestBody PricingRuleUpdateDto dto) {
        return ResponseEntity.ok(service.update(publicId, dto));
    }

    @Operation(summary = "Patch pricing rule")
    @PatchMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.PRICING_RULE_UPDATE + "')")
    public ResponseEntity<PricingRuleResponseDto> patch(@PathVariable UUID publicId, @Validated @RequestBody PricingRulePatchDto dto) {
        return ResponseEntity.ok(service.patch(publicId, dto));
    }

    @Operation(summary = "Delete pricing rule")
    @DeleteMapping("/{publicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.PRICING_RULE_DELETE + "')")
    public ResponseEntity<Void> delete(@PathVariable UUID publicId) {
        service.delete(publicId);
        return ResponseEntity.noContent().build();
    }
}

