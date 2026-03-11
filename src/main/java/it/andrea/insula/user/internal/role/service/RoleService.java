package it.andrea.insula.user.internal.role.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.user.internal.permission.model.Permission;
import it.andrea.insula.user.internal.permission.model.PermissionRepository;
import it.andrea.insula.user.internal.role.dto.request.RoleCreateDto;
import it.andrea.insula.user.internal.role.dto.request.RolePatchDto;
import it.andrea.insula.user.internal.role.dto.request.RoleSearchCriteria;
import it.andrea.insula.user.internal.role.dto.request.RoleUpdateDto;
import it.andrea.insula.user.internal.role.dto.response.RoleResponseDto;
import it.andrea.insula.user.internal.role.mapper.RoleCreateDtoToRoleMapper;
import it.andrea.insula.user.internal.role.mapper.RolePatchMapper;
import it.andrea.insula.user.internal.role.mapper.RoleToRoleResponseDtoMapper;
import it.andrea.insula.user.internal.role.mapper.RoleUpdateMapper;
import it.andrea.insula.user.internal.role.model.Role;
import it.andrea.insula.user.internal.role.model.RoleRepository;
import it.andrea.insula.user.internal.role.model.RoleSpecification;
import it.andrea.insula.user.internal.user.exception.UserErrorCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleValidator roleValidator;

    private final RoleCreateDtoToRoleMapper roleCreateDtoToRoleMapper;
    private final RoleUpdateMapper roleUpdateMapper;
    private final RolePatchMapper rolePatchMapper;
    private final RoleToRoleResponseDtoMapper roleToRoleResponseDtoMapper;

    @Transactional(readOnly = true)
    public RoleResponseDto getByName(String name) {
        Role role = retrieveRoleByName(name);
        return roleToRoleResponseDtoMapper.apply(role);
    }

    @Transactional(readOnly = true)
    public PageResponse<RoleResponseDto> getAll(RoleSearchCriteria criteria, Pageable pageable) {
        Specification<Role> spec = RoleSpecification.withCriteria(criteria);
        Page<RoleResponseDto> page = roleRepository.findAll(spec, pageable)
                .map(roleToRoleResponseDtoMapper);
        return PageResponse.fromPage(page);
    }

    @Transactional(readOnly = true)
    public List<RoleResponseDto> findAll(RoleSearchCriteria criteria) {
        Specification<Role> spec = RoleSpecification.withCriteria(criteria);
        List<Role> roles = roleRepository.findAll(spec);
        return roles.stream()
                .map(roleToRoleResponseDtoMapper)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoleResponseDto createRole(RoleCreateDto request) {
        roleValidator.validateCreate(request.name());

        Role role = roleCreateDtoToRoleMapper.apply(request);

        Set<Permission> foundPermissions = computePermissions(request.permissions());
        role.setPermissions(foundPermissions);

        Role savedRole = roleRepository.save(role);
        return roleToRoleResponseDtoMapper.apply(savedRole);
    }

    @Transactional
    public RoleResponseDto updateRole(String name, RoleUpdateDto request) {
        Role role = retrieveRoleByName(name);

        roleValidator.validateNotAssignedToAdmin(role);
        roleValidator.validateUpdate(role.getId(), request.name(), role.getName());

        roleUpdateMapper.apply(request, role);

        Set<Permission> foundPermissions = computePermissions(request.permissions());
        role.setPermissions(foundPermissions);

        Role savedRole = roleRepository.save(role);
        return roleToRoleResponseDtoMapper.apply(savedRole);
    }

    @Transactional
    public RoleResponseDto patchRole(String name, RolePatchDto request) {
        Role role = retrieveRoleByName(name);

        roleValidator.validateNotAssignedToAdmin(role);
        roleValidator.validateUpdate(role.getId(), request.name(), role.getName());

        rolePatchMapper.apply(request, role);

        if (request.permissions() != null) {
            Set<Permission> foundPermissions = computePermissions(request.permissions());
            role.setPermissions(foundPermissions);
        }

        Role savedRole = roleRepository.save(role);
        return roleToRoleResponseDtoMapper.apply(savedRole);
    }

    @Transactional
    public void deleteRole(String name) {
        Role role = retrieveRoleByName(name);
        roleValidator.validateNotAssignedToAdmin(role);
        roleValidator.validateDelete(role);
        roleRepository.delete(role);
    }

    private Role retrieveRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(UserErrorCodes.ROLE_NOT_FOUND, name));
    }

    private Set<Permission> computePermissions(Collection<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return new HashSet<>();
        }

        List<Permission> foundPermissions = permissionRepository.findAllById(permissionIds);
        if (foundPermissions.size() != new HashSet<>(permissionIds).size()) {
            Set<Long> foundIds = foundPermissions.stream()
                    .map(Permission::getId)
                    .collect(Collectors.toSet());
            List<Long> missingIds = permissionIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new ResourceNotFoundException(UserErrorCodes.PERMISSION_NOT_FOUND, missingIds);
        }

        return new HashSet<>(foundPermissions);
    }
}
