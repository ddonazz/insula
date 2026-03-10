package it.andrea.insula.agency.internal.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.andrea.insula.agency.internal.dto.request.AgencyCreateDto;
import it.andrea.insula.agency.internal.dto.request.AgencySearchCriteria;
import it.andrea.insula.agency.internal.dto.request.AgencyUpdateDto;
import it.andrea.insula.agency.internal.dto.response.AgencyResponseDto;
import it.andrea.insula.agency.internal.service.AgencyService;
import it.andrea.insula.core.dto.PageResponse;
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
@RequestMapping("/api/v1/agencies")
@RequiredArgsConstructor
@Tag(name = "Agency Management", description = "APIs for managing agencies")
public class AgencyController {

    private final AgencyService agencyService;

    @Operation(summary = "Get an agency by Public ID (UUID)")
    @GetMapping("/{publicId}")
    @PreAuthorize("hasAuthority('agency:read')")
    public ResponseEntity<AgencyResponseDto> getByPublicId(@PathVariable UUID publicId) {
        AgencyResponseDto agency = agencyService.getByPublicId(publicId);
        return ResponseEntity.ok(agency);
    }

    @Operation(summary = "Get all agencies (paginated)")
    @GetMapping
    @PreAuthorize("hasAuthority('agency:read')")
    public ResponseEntity<PageResponse<AgencyResponseDto>> getAll(
            @ParameterObject AgencySearchCriteria criteria,
            @ParameterObject @PageableDefault(size = 20, sort = "name") Pageable pageable
    ) {
        PageResponse<AgencyResponseDto> response = agencyService.getAll(criteria, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all agencies as a list")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('agency:read')")
    public ResponseEntity<List<AgencyResponseDto>> getList(@ParameterObject AgencySearchCriteria criteria) {
        List<AgencyResponseDto> response = agencyService.findAll(criteria);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create a new agency")
    @PostMapping
    @PreAuthorize("hasAuthority('agency:create')")
    public ResponseEntity<AgencyResponseDto> create(@Validated @RequestBody AgencyCreateDto dto) {
        AgencyResponseDto createdAgency = agencyService.create(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{publicId}")
                .buildAndExpand(createdAgency.publicId())
                .toUri();

        return ResponseEntity.created(location).body(createdAgency);
    }

    @Operation(summary = "Update an existing agency")
    @PutMapping("/{publicId}")
    @PreAuthorize("hasAuthority('agency:update')")
    public ResponseEntity<AgencyResponseDto> update(@PathVariable UUID publicId, @Validated @RequestBody AgencyUpdateDto dto) {
        AgencyResponseDto updatedAgency = agencyService.update(publicId, dto);
        return ResponseEntity.ok(updatedAgency);
    }

    @Operation(summary = "Delete an agency")
    @DeleteMapping("/{publicId}")
    @PreAuthorize("hasAuthority('agency:delete')")
    public ResponseEntity<Void> delete(@PathVariable UUID publicId) {
        agencyService.delete(publicId);
        return ResponseEntity.noContent().build();
    }
}

