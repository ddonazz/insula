package it.andrea.insula.user.internal.user.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.user.internal.user.dto.request.UserCreateDto;
import it.andrea.insula.user.internal.user.dto.request.UserProfileUpdateDto;
import it.andrea.insula.user.internal.user.dto.request.UserSearchCriteria;
import it.andrea.insula.user.internal.user.dto.request.UserUpdateDto;
import it.andrea.insula.user.internal.user.dto.response.UserResponseDto;
import it.andrea.insula.user.internal.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Get a user by Internal ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {
        UserResponseDto userDto = userService.getById(id);
        return ResponseEntity.ok(userDto);
    }

    @Operation(summary = "Get a user by Public ID (UUID)")
    @GetMapping("/public/{publicId}")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<UserResponseDto> getByPublicId(@PathVariable UUID publicId) {
        UserResponseDto userDto = userService.getByPublicId(publicId);
        return ResponseEntity.ok(userDto);
    }

    @Operation(summary = "Get all users")
    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<PageResponse<UserResponseDto>> getAll(
            @ParameterObject UserSearchCriteria criteria,
            @ParameterObject @PageableDefault(size = 20, sort = "username") Pageable pageable
    ) {
        PageResponse<UserResponseDto> response = userService.getAll(criteria, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all users as a list")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<List<UserResponseDto>> getList(@ParameterObject UserSearchCriteria criteria) {
        List<UserResponseDto> response = userService.findAll(criteria);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create a new user")
    @PostMapping
    @PreAuthorize("hasAuthority('user:create')")
    public ResponseEntity<UserResponseDto> create(@Validated @RequestBody UserCreateDto dto) {
        UserResponseDto createdUser = userService.create(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdUser.id())
                .toUri();

        return ResponseEntity.created(location).body(createdUser);
    }

    @Operation(summary = "Update an existing user")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<UserResponseDto> update(@PathVariable Long id, @Validated @RequestBody UserUpdateDto dto) {
        UserResponseDto updatedUser = userService.update(id, dto);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Activate a user")
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        userService.activateUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Suspend a user")
    @PutMapping("/{id}/suspend")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<Void> suspend(@PathVariable Long id) {
        userService.suspendUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a user")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Ottieni i dati dell'utente attualmente loggato (whoami)")
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> whoami(@AuthenticationPrincipal UserDetails userDetails) {
        UserResponseDto userDto = userService.getByUsername(userDetails.getUsername());
        return ResponseEntity.ok(userDto);
    }

    @Operation(summary = "Aggiorna il profilo dell'utente attualmente loggato")
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> updateMyProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Validated @RequestBody UserProfileUpdateDto dto
    ) {
        UserResponseDto userDto = userService.updateProfile(userDetails.getUsername(), dto);
        return ResponseEntity.ok(userDto);
    }
}
