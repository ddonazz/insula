package it.andrea.insula.owner.internal.agreement.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.owner.internal.agreement.dto.request.AgreementCreateDto;
import it.andrea.insula.owner.internal.agreement.dto.request.AgreementPatchDto;
import it.andrea.insula.owner.internal.agreement.dto.request.AgreementSearchCriteria;
import it.andrea.insula.owner.internal.agreement.dto.request.AgreementUpdateDto;
import it.andrea.insula.owner.internal.agreement.dto.response.AgreementResponseDto;
import it.andrea.insula.owner.internal.agreement.service.AgreementService;
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
@RequestMapping("/api/v1/owners/{ownerPublicId}/agreements")
@RequiredArgsConstructor
@Tag(name = "Agreement Management", description = "APIs for managing management agreements between owners and units")
public class AgreementController {

    private final AgreementService service;

    @Operation(summary = "Get an agreement by Public ID")
    @GetMapping("/{agreementPublicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.AGREEMENT_READ + "')")
    public ResponseEntity<AgreementResponseDto> getByPublicId(
            @PathVariable UUID ownerPublicId,
            @PathVariable UUID agreementPublicId
    ) {
        return ResponseEntity.ok(service.getByPublicId(ownerPublicId, agreementPublicId));
    }

    @Operation(summary = "Get all agreements for an owner (paginated)")
    @GetMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.AGREEMENT_READ + "')")
    public ResponseEntity<PageResponse<AgreementResponseDto>> getAll(
            @PathVariable UUID ownerPublicId,
            @ParameterObject AgreementSearchCriteria criteria,
            @ParameterObject @PageableDefault(size = 20, sort = "startDate") Pageable pageable
    ) {
        return ResponseEntity.ok(service.getAll(ownerPublicId, criteria, pageable));
    }

    @Operation(summary = "Get all agreements for an owner as a list")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.AGREEMENT_READ + "')")
    public ResponseEntity<List<AgreementResponseDto>> getList(
            @PathVariable UUID ownerPublicId,
            @ParameterObject AgreementSearchCriteria criteria
    ) {
        return ResponseEntity.ok(service.findAll(ownerPublicId, criteria));
    }

    @Operation(summary = "Create a new agreement for an owner")
    @PostMapping
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.AGREEMENT_CREATE + "')")
    public ResponseEntity<AgreementResponseDto> create(
            @PathVariable UUID ownerPublicId,
            @Validated @RequestBody AgreementCreateDto dto
    ) {
        AgreementResponseDto created = service.create(ownerPublicId, dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{agreementPublicId}")
                .buildAndExpand(created.publicId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Update an existing agreement")
    @PutMapping("/{agreementPublicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.AGREEMENT_UPDATE + "')")
    public ResponseEntity<AgreementResponseDto> update(
            @PathVariable UUID ownerPublicId,
            @PathVariable UUID agreementPublicId,
            @Validated @RequestBody AgreementUpdateDto dto
    ) {
        return ResponseEntity.ok(service.update(ownerPublicId, agreementPublicId, dto));
    }

    @Operation(summary = "Patch an existing agreement")
    @PatchMapping("/{agreementPublicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.AGREEMENT_UPDATE + "')")
    public ResponseEntity<AgreementResponseDto> patch(
            @PathVariable UUID ownerPublicId,
            @PathVariable UUID agreementPublicId,
            @Validated @RequestBody AgreementPatchDto dto
    ) {
        return ResponseEntity.ok(service.patch(ownerPublicId, agreementPublicId, dto));
    }

    @Operation(summary = "Delete an agreement")
    @DeleteMapping("/{agreementPublicId}")
    @PreAuthorize("hasAuthority('" + PermissionAuthority.Constants.AGREEMENT_DELETE + "')")
    public ResponseEntity<Void> delete(
            @PathVariable UUID ownerPublicId,
            @PathVariable UUID agreementPublicId
    ) {
        service.delete(ownerPublicId, agreementPublicId);
        return ResponseEntity.noContent().build();
    }
}

