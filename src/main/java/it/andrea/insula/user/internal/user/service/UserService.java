package it.andrea.insula.user.internal.user.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.user.internal.role.model.Role;
import it.andrea.insula.user.internal.role.model.RoleRepository;
import it.andrea.insula.user.internal.user.dto.request.UserCreateDto;
import it.andrea.insula.user.internal.user.dto.request.UserProfileUpdateDto;
import it.andrea.insula.user.internal.user.dto.request.UserSearchCriteria;
import it.andrea.insula.user.internal.user.dto.request.UserUpdateDto;
import it.andrea.insula.user.internal.user.dto.response.UserResponseDto;
import it.andrea.insula.user.internal.user.exception.UserErrorCodes;
import it.andrea.insula.user.internal.user.mapper.UserCreateDtoToUserMapper;
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

    private final UserCreateDtoToUserMapper createMapper;
    private final UserToUserResponseDtoMapper responseMapper;

    @Transactional
    public UserResponseDto create(UserCreateDto dto) {
        userValidator.validateCreate(dto.username(), dto.email());

        User user = createMapper.apply(dto);
        user.setPassword(passwordEncoder.encode(dto.password()));

        Set<Role> roles = fetchRolesByIds(dto.roles());
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        return responseMapper.apply(savedUser);
    }

    @Transactional
    public UserResponseDto update(Long id, UserUpdateDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(UserErrorCodes.USER_NOT_FOUND, id));

        userValidator.validateUpdate(id, dto.username(), user.getUsername(), dto.email(), user.getEmail());

        if (dto.username() != null) {
            user.setUsername(dto.username());
        }

        if (dto.email() != null) {
            user.setEmail(dto.email());
        }

        if (dto.status() != null) {
            user.setStatus(dto.status());
        }

        if (dto.roles() != null) {
            Set<Role> roles = fetchRolesByIds(dto.roles());
            user.setRoles(roles);
        }

        User updatedUser = userRepository.save(user);
        return responseMapper.apply(updatedUser);
    }

    @Transactional
    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(UserErrorCodes.USER_NOT_FOUND, id));
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    @Transactional
    public void suspendUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(UserErrorCodes.USER_NOT_FOUND, id));
        user.setStatus(UserStatus.SUSPENDED);
        userRepository.save(user);
    }

    public UserResponseDto getById(Long id) {
        return userRepository.findById(id)
                .map(responseMapper)
                .orElseThrow(() -> new ResourceNotFoundException(UserErrorCodes.USER_NOT_FOUND, id));
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
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(UserErrorCodes.USER_NOT_FOUND, id));
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

        if (dto.email() != null) {
            user.setEmail(dto.email());
        }

        User updatedUser = userRepository.save(user);
        return responseMapper.apply(updatedUser);
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
