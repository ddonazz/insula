package it.andrea.insula.user.internal.user.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.user.internal.role.model.Role;
import it.andrea.insula.user.internal.role.model.RoleRepository;
import it.andrea.insula.user.internal.user.dto.request.UserCreateDto;
import it.andrea.insula.user.internal.user.dto.request.UserPatchDto;
import it.andrea.insula.user.internal.user.dto.request.UserProfileUpdateDto;
import it.andrea.insula.user.internal.user.dto.request.UserSearchCriteria;
import it.andrea.insula.user.internal.user.dto.response.UserResponseDto;
import it.andrea.insula.user.internal.user.exception.UserErrorCodes;
import it.andrea.insula.user.internal.user.mapper.UserCreateDtoToUserMapper;
import it.andrea.insula.user.internal.user.mapper.UserPatchMapper;
import it.andrea.insula.user.internal.user.mapper.UserProfilePatchMapper;
import it.andrea.insula.user.internal.user.mapper.UserToUserResponseDtoMapper;
import it.andrea.insula.user.internal.user.model.User;
import it.andrea.insula.user.internal.user.model.UserRepository;
import it.andrea.insula.user.internal.user.model.UserSpecification;
import it.andrea.insula.user.internal.user.model.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;
    private final AdminGuard adminGuard;

    private final UserCreateDtoToUserMapper createMapper;
    private final UserPatchMapper patchMapper;
    private final UserProfilePatchMapper profilePatchMapper;
    private final UserToUserResponseDtoMapper responseMapper;

    @Transactional
    public UserResponseDto create(UserCreateDto dto) {
        userValidator.validateCreate(dto.username(), dto.email());

        User user = createMapper.apply(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));

        Set<Role> roles = fetchRolesByIds(dto.roles());
        user.setRoles(roles);

        userValidator.validateTenantConstraints(user);

        User savedUser = userRepository.save(user);
        return responseMapper.apply(savedUser);
    }

    @Transactional
    public UserResponseDto patch(UUID publicId, UserPatchDto dto) {
        User user = findByPublicIdOrThrow(publicId);

        adminGuard.assertNotAdmin(user);

        userValidator.validateUpdate(user.getId(), dto.username(), user.getUsername(), dto.email(), user.getEmail());

        patchMapper.apply(dto, user);

        if (dto.roles() != null) {
            Set<Role> roles = fetchRolesByIds(dto.roles());
            user.setRoles(roles);
        }

        User updatedUser = userRepository.save(user);
        return responseMapper.apply(updatedUser);
    }

    @Transactional
    public void activateUser(UUID publicId) {
        User user = findByPublicIdOrThrow(publicId);
        adminGuard.assertNotAdmin(user);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    @Transactional
    public void suspendUser(UUID publicId) {
        User user = findByPublicIdOrThrow(publicId);
        adminGuard.assertNotAdmin(user);
        user.setStatus(UserStatus.SUSPENDED);
        userRepository.save(user);
    }

    public UserResponseDto getByPublicId(UUID publicId) {
        return userRepository.findByPublicId(publicId)
                .map(responseMapper)
                .orElseThrow(() -> new ResourceNotFoundException(UserErrorCodes.USER_NOT_FOUND, publicId));
    }

    public PageResponse<UserResponseDto> getAll(UserSearchCriteria criteria, Pageable pageable) {
        Specification<User> spec = UserSpecification.withCriteria(criteria);
        return PageResponse.fromPage(userRepository.findAll(spec, pageable)
                .map(responseMapper)
        );
    }

    public List<UserResponseDto> findAll(UserSearchCriteria criteria) {
        Specification<User> spec = UserSpecification.withCriteria(criteria);
        return userRepository.findAll(spec).stream()
                .map(responseMapper)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(UUID publicId) {
        User user = findByPublicIdOrThrow(publicId);
        adminGuard.assertNotAdmin(user);
        user.delete();
        userRepository.save(user);
    }

    public UserResponseDto getByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(responseMapper)
                .orElseThrow(() -> new ResourceNotFoundException(UserErrorCodes.USER_NOT_FOUND, username));
    }

    @Transactional
    public UserResponseDto updateProfile(String username, UserProfileUpdateDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(UserErrorCodes.USER_NOT_FOUND, username));

        userValidator.validateEmailUpdate(user.getId(), dto.email(), user.getEmail());

        profilePatchMapper.apply(dto, user);

        User updatedUser = userRepository.save(user);
        return responseMapper.apply(updatedUser);
    }

    private User findByPublicIdOrThrow(UUID publicId) {
        return userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResourceNotFoundException(UserErrorCodes.USER_NOT_FOUND, publicId));
    }

    private Set<Role> fetchRolesByIds(Set<Long> roleIds) {
        List<Role> roles = roleRepository.findAllById(roleIds);

        if (roles.size() != roleIds.size()) {
            Set<Long> foundIds = roles.stream()
                    .map(Role::getId)
                    .collect(Collectors.toSet());

            List<Long> missingIds = roleIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();

            throw new ResourceNotFoundException(UserErrorCodes.ROLE_NOT_FOUND, missingIds);
        }

        return new HashSet<>(roles);
    }
}
