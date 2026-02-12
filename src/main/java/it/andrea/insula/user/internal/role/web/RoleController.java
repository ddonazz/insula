package it.andrea.insula.user.internal.role.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.user.internal.role.dto.request.RoleCreateDto;
import it.andrea.insula.user.internal.role.dto.request.RoleUpdateDto;
import it.andrea.insula.user.internal.role.dto.response.RoleResponseDto;
import it.andrea.insula.user.internal.role.service.RoleService;
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

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Role Management", description = "APIs for managing user roles")
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "Get a role by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<RoleResponseDto> getRole(@PathVariable Long id) {
        RoleResponseDto response = roleService.getRole(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all roles")
    @GetMapping
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<PageResponse<RoleResponseDto>> getRoles(
            @ParameterObject
            @PageableDefault(size = 20, sort = "name") Pageable pageable
    ) {
        PageResponse<RoleResponseDto> response = roleService.getRoles(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create a new role")
    @PostMapping
    @PreAuthorize("hasAuthority('role:create')")
    public ResponseEntity<RoleResponseDto> createRole(@Validated @RequestBody RoleCreateDto request) {
        RoleResponseDto response = roleService.createRole(request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Update an existing role")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<RoleResponseDto> updateRole(
            @PathVariable Long id,
            @Validated @RequestBody RoleUpdateDto roleUpdateDto
    ) {
        RoleResponseDto updateRole = roleService.updateRole(id, roleUpdateDto);
        return ResponseEntity.ok(updateRole);
    }

    @Operation(summary = "Delete a role")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('role:delete')")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}