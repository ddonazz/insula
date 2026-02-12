package it.andrea.insula.user.internal.user.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.exception.ResourceInUseException;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.user.internal.role.model.Role;
import it.andrea.insula.user.internal.role.model.RoleRepository;
import it.andrea.insula.user.internal.user.dto.request.UserCreateDto;
import it.andrea.insula.user.internal.user.dto.request.UserUpdateDto;
import it.andrea.insula.user.internal.user.dto.response.UserResponseDto;
import it.andrea.insula.user.internal.user.exception.UserErrorCodes;
import it.andrea.insula.user.internal.user.mapper.UserCreateDtoToUserMapper;
import it.andrea.insula.user.internal.user.mapper.UserToUserResponseDtoMapper;
import it.andrea.insula.user.internal.user.model.User;
import it.andrea.insula.user.internal.user.model.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private final UserCreateDtoToUserMapper createMapper;
    private final UserToUserResponseDtoMapper responseMapper;

    @Transactional
    public UserResponseDto create(UserCreateDto dto) {
        if (userRepository.existsByUsername(dto.username())) {
            throw new ResourceInUseException(UserErrorCodes.USERNAME_ALREADY_EXISTS, dto.username());
        }

        if (userRepository.existsByEmail(dto.email())) {
            throw new ResourceInUseException(UserErrorCodes.EMAIL_ALREADY_EXISTS, dto.email());
        }

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

        if (dto.roles() != null && !dto.roles().isEmpty()) {
            Set<Role> roles = fetchRolesByIds(dto.roles());
            user.setRoles(roles);
        }

        User updatedUser = userRepository.save(user);
        return responseMapper.apply(updatedUser);
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

    public List<UserResponseDto> getAll() {
        return userRepository.findAll().stream()
                .map(responseMapper)
                .collect(Collectors.toList());
    }

    public PageResponse<UserResponseDto> getAll(Pageable pageable) {
        Page<User> usersPage = userRepository.findAll(pageable);

        Page<UserResponseDto> dtoPage = usersPage.map(responseMapper);

        return PageResponse.fromPage(dtoPage);
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException(UserErrorCodes.USER_NOT_FOUND, id);
        }
        userRepository.deleteById(id);
    }

    private Set<Role> fetchRolesByIds(Set<Long> roleIds) {
        List<Role> roles = roleRepository.findAllById(roleIds);

        if (roles.size() != roleIds.size()) {
            Set<Long> foundIds = roles.stream().map(Role::getId).collect(Collectors.toSet());
            List<Long> missingIds = roleIds.stream().filter(id -> !foundIds.contains(id)).toList();

            throw new ResourceNotFoundException(UserErrorCodes.ROLE_NOT_FOUND, missingIds);
        }

        return new HashSet<>(roles);
    }
}