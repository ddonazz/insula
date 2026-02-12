package it.andrea.insula.user.internal.permission.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.user.internal.permission.dto.response.PermissionResponseDto;
import it.andrea.insula.user.internal.permission.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@Tag(name = "Permission Management", description = "APIs for viewing system permissions")
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "Retrieve available permissions")
    @GetMapping
    @PreAuthorize("hasAuthority('permission:read')")
    public ResponseEntity<PageResponse<PermissionResponseDto>> getAll(
            @ParameterObject
            @PageableDefault(size = 50, sort = "authority") Pageable pageable
    ) {
        PageResponse<PermissionResponseDto> response = permissionService.getAll(pageable);
        return ResponseEntity.ok(response);
    }
}