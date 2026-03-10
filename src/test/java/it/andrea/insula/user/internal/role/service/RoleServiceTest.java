package it.andrea.insula.user.internal.role.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.ResourceInUseException;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.user.internal.permission.model.Permission;
import it.andrea.insula.user.internal.permission.model.PermissionRepository;
import it.andrea.insula.user.internal.role.dto.request.RoleCreateDto;
import it.andrea.insula.user.internal.role.dto.request.RoleUpdateDto;
import it.andrea.insula.user.internal.role.dto.response.RoleResponseDto;
import it.andrea.insula.user.internal.role.mapper.RoleCreateDtoToRoleMapper;
import it.andrea.insula.user.internal.role.mapper.RoleToRoleResponseDtoMapper;
import it.andrea.insula.user.internal.role.model.Role;
import it.andrea.insula.user.internal.role.model.RoleRepository;
import it.andrea.insula.user.internal.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PermissionRepository permissionRepository;
    @Mock
    private RoleToRoleResponseDtoMapper roleResponseMapper;
    @Mock
    private RoleCreateDtoToRoleMapper roleCreateMapper;

    @InjectMocks
    private RoleService roleService;

    private Role role;
    private RoleResponseDto responseDto;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName("ADMIN");
        role.setDescription("Admin role");
        role.setPermissions(new HashSet<>());
        role.setUsers(new HashSet<>());

        responseDto = new RoleResponseDto(1L, "ADMIN", "Admin role", Collections.emptySet());
    }

    @Test
    void getById_shouldReturnRole() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleResponseMapper.apply(role)).thenReturn(responseDto);

        RoleResponseDto result = roleService.getById(1L);

        assertThat(result.name()).isEqualTo("ADMIN");
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createRole_shouldCreateSuccessfully() {
        RoleCreateDto dto = new RoleCreateDto("ADMIN", "Admin role", Set.of(1L));
        Permission perm = Permission.builder().id(1L).authority("user:read").description("Read").domain("USER").build();

        when(roleRepository.existsByName("ADMIN")).thenReturn(false);
        when(roleCreateMapper.apply(dto)).thenReturn(role);
        when(permissionRepository.findAllById(Set.of(1L))).thenReturn(List.of(perm));
        when(roleRepository.save(role)).thenReturn(role);
        when(roleResponseMapper.apply(role)).thenReturn(responseDto);

        RoleResponseDto result = roleService.createRole(dto);

        assertThat(result.name()).isEqualTo("ADMIN");
        verify(roleRepository).save(role);
    }

    @Test
    void createRole_shouldThrowWhenNameExists() {
        RoleCreateDto dto = new RoleCreateDto("ADMIN", "Admin role", Set.of(1L));
        when(roleRepository.existsByName("ADMIN")).thenReturn(true);

        assertThatThrownBy(() -> roleService.createRole(dto))
                .isInstanceOf(ResourceInUseException.class);
    }

    @Test
    void createRole_shouldThrowWhenPermissionsNotFound() {
        RoleCreateDto dto = new RoleCreateDto("ADMIN", "Admin role", Set.of(1L, 99L));
        Permission perm = Permission.builder().id(1L).authority("user:read").description("Read").domain("USER").build();

        when(roleRepository.existsByName("ADMIN")).thenReturn(false);
        when(roleCreateMapper.apply(dto)).thenReturn(role);
        when(permissionRepository.findAllById(Set.of(1L, 99L))).thenReturn(List.of(perm));

        assertThatThrownBy(() -> roleService.createRole(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateRole_shouldUpdateSuccessfully() {
        RoleUpdateDto dto = new RoleUpdateDto("SUPERADMIN", "Super admin", null);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.existsByNameAndIdNot("SUPERADMIN", 1L)).thenReturn(false);
        when(roleRepository.save(role)).thenReturn(role);
        when(roleResponseMapper.apply(role)).thenReturn(responseDto);

        RoleResponseDto result = roleService.updateRole(1L, dto);

        assertThat(result).isNotNull();
        assertThat(role.getName()).isEqualTo("SUPERADMIN");
    }

    @Test
    void updateRole_shouldThrowWhenNameTaken() {
        RoleUpdateDto dto = new RoleUpdateDto("TAKEN", null, null);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.existsByNameAndIdNot("TAKEN", 1L)).thenReturn(true);

        assertThatThrownBy(() -> roleService.updateRole(1L, dto))
                .isInstanceOf(ResourceInUseException.class);
    }

    @Test
    void updateRole_shouldSkipNameCheckWhenUnchanged() {
        RoleUpdateDto dto = new RoleUpdateDto("ADMIN", "Updated desc", null);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.save(role)).thenReturn(role);
        when(roleResponseMapper.apply(role)).thenReturn(responseDto);

        roleService.updateRole(1L, dto);

        verify(roleRepository, never()).existsByNameAndIdNot(any(), any());
        assertThat(role.getDescription()).isEqualTo("Updated desc");
    }

    @Test
    void deleteRole_shouldDeleteSuccessfully() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        roleService.deleteRole(1L);

        verify(roleRepository).delete(role);
    }

    @Test
    void deleteRole_shouldThrowWhenRoleInUse() {
        User user = new User();
        role.getUsers().add(user);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        assertThatThrownBy(() -> roleService.deleteRole(1L))
                .isInstanceOf(ResourceInUseException.class);
    }

    @Test
    void deleteRole_shouldThrowWhenNotFound() {
        when(roleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roleService.deleteRole(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAll_shouldReturnPageResponse() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Role> page = new PageImpl<>(List.of(role), pageable, 1);
        when(roleRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(roleResponseMapper.apply(role)).thenReturn(responseDto);

        PageResponse<RoleResponseDto> result = roleService.getAll(null, pageable);

        assertThat(result.content()).hasSize(1);
    }
}
