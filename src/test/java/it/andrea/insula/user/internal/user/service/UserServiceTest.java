package it.andrea.insula.user.internal.user.service;

import it.andrea.insula.core.dto.PageResponse;
import it.andrea.insula.core.dto.TranslatedEnum;
import it.andrea.insula.core.exception.ResourceInUseException;
import it.andrea.insula.core.exception.ResourceNotFoundException;
import it.andrea.insula.user.internal.role.model.Role;
import it.andrea.insula.user.internal.role.model.RoleRepository;
import it.andrea.insula.user.internal.user.dto.request.UserCreateDto;
import it.andrea.insula.user.internal.user.dto.request.UserProfileUpdateDto;
import it.andrea.insula.user.internal.user.dto.request.UserSearchCriteria;
import it.andrea.insula.user.internal.user.dto.request.UserUpdateDto;
import it.andrea.insula.user.internal.user.dto.response.UserResponseDto;
import it.andrea.insula.user.internal.user.mapper.UserCreateDtoToUserMapper;
import it.andrea.insula.user.internal.user.mapper.UserToUserResponseDtoMapper;
import it.andrea.insula.user.internal.user.model.User;
import it.andrea.insula.user.internal.user.model.UserRepository;
import it.andrea.insula.user.internal.user.model.UserStatus;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserValidator userValidator;
    @Mock
    private UserCreateDtoToUserMapper createMapper;
    @Mock
    private UserToUserResponseDtoMapper responseMapper;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserResponseDto responseDto;
    private UUID publicId;

    @BeforeEach
    void setUp() {
        publicId = UUID.randomUUID();
        user = new User();
        user.setId(1L);
        user.setPublicId(publicId);
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setPassword("encoded");
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(new HashSet<>());

        responseDto = UserResponseDto.builder()
                .id(1L)
                .publicId(publicId)
                .username("testuser")
                .email("test@test.com")
                .status(new TranslatedEnum("ACTIVE", "Active"))
                .roles(Collections.emptySet())
                .build();
    }

    // === CREATE ===

    @Test
    void create_shouldCreateUserSuccessfully() {
        UserCreateDto dto = new UserCreateDto("testuser", "test@test.com", "password123", Set.of(1L));
        Role role = new Role();
        role.setId(1L);
        role.setName("USER");
        role.setPermissions(new HashSet<>());

        doNothing().when(userValidator).validateCreate("testuser", "test@test.com");
        when(createMapper.apply(dto)).thenReturn(user);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(roleRepository.findAllById(Set.of(1L))).thenReturn(List.of(role));
        when(userRepository.save(user)).thenReturn(user);
        when(responseMapper.apply(user)).thenReturn(responseDto);

        UserResponseDto result = userService.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo("testuser");
        verify(userRepository).save(user);
    }

    @Test
    void create_shouldThrowWhenUsernameAlreadyExists() {
        UserCreateDto dto = new UserCreateDto("testuser", "test@test.com", "password123", Set.of(1L));

        doThrow(new ResourceInUseException(it.andrea.insula.user.internal.user.exception.UserErrorCodes.USERNAME_ALREADY_EXISTS, "testuser"))
                .when(userValidator).validateCreate("testuser", "test@test.com");

        assertThatThrownBy(() -> userService.create(dto))
                .isInstanceOf(ResourceInUseException.class);
    }

    @Test
    void create_shouldThrowWhenEmailAlreadyExists() {
        UserCreateDto dto = new UserCreateDto("testuser", "test@test.com", "password123", Set.of(1L));

        doThrow(new ResourceInUseException(it.andrea.insula.user.internal.user.exception.UserErrorCodes.EMAIL_ALREADY_EXISTS, "test@test.com"))
                .when(userValidator).validateCreate("testuser", "test@test.com");

        assertThatThrownBy(() -> userService.create(dto))
                .isInstanceOf(ResourceInUseException.class);
    }

    @Test
    void create_shouldThrowWhenRolesNotFound() {
        UserCreateDto dto = new UserCreateDto("testuser", "test@test.com", "password123", Set.of(1L, 99L));

        doNothing().when(userValidator).validateCreate("testuser", "test@test.com");
        when(createMapper.apply(dto)).thenReturn(user);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        Role role = new Role();
        role.setId(1L);
        when(roleRepository.findAllById(Set.of(1L, 99L))).thenReturn(List.of(role));

        assertThatThrownBy(() -> userService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // === UPDATE ===

    @Test
    void update_shouldUpdateUserSuccessfully() {
        UserUpdateDto dto = new UserUpdateDto("newuser", "new@test.com", null, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userValidator).validateUpdate(1L, "newuser", "testuser", "new@test.com", "test@test.com");
        when(userRepository.save(user)).thenReturn(user);
        when(responseMapper.apply(user)).thenReturn(responseDto);

        UserResponseDto result = userService.update(1L, dto);

        assertThat(result).isNotNull();
        assertThat(user.getUsername()).isEqualTo("newuser");
        assertThat(user.getEmail()).isEqualTo("new@test.com");
    }

    @Test
    void update_shouldThrowWhenUserNotFound() {
        UserUpdateDto dto = new UserUpdateDto("newuser", null, null, null);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(99L, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_shouldThrowWhenNewUsernameAlreadyTaken() {
        UserUpdateDto dto = new UserUpdateDto("taken", null, null, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doThrow(new ResourceInUseException(it.andrea.insula.user.internal.user.exception.UserErrorCodes.USERNAME_ALREADY_EXISTS, "taken"))
                .when(userValidator).validateUpdate(1L, "taken", "testuser", null, "test@test.com");

        assertThatThrownBy(() -> userService.update(1L, dto))
                .isInstanceOf(ResourceInUseException.class);
    }

    @Test
    void update_shouldThrowWhenNewEmailAlreadyTaken() {
        UserUpdateDto dto = new UserUpdateDto(null, "taken@test.com", null, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doThrow(new ResourceInUseException(it.andrea.insula.user.internal.user.exception.UserErrorCodes.EMAIL_ALREADY_EXISTS, "taken@test.com"))
                .when(userValidator).validateUpdate(1L, null, "testuser", "taken@test.com", "test@test.com");

        assertThatThrownBy(() -> userService.update(1L, dto))
                .isInstanceOf(ResourceInUseException.class);
    }

    @Test
    void update_shouldUpdateStatus() {
        UserUpdateDto dto = new UserUpdateDto(null, null, UserStatus.SUSPENDED, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userValidator).validateUpdate(1L, null, "testuser", null, "test@test.com");
        when(userRepository.save(user)).thenReturn(user);
        when(responseMapper.apply(user)).thenReturn(responseDto);

        userService.update(1L, dto);

        assertThat(user.getStatus()).isEqualTo(UserStatus.SUSPENDED);
    }

    @Test
    void update_shouldSkipWhenUsernameNotChanged() {
        UserUpdateDto dto = new UserUpdateDto("testuser", null, null, null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userValidator).validateUpdate(1L, "testuser", "testuser", null, "test@test.com");
        when(userRepository.save(user)).thenReturn(user);
        when(responseMapper.apply(user)).thenReturn(responseDto);

        userService.update(1L, dto);

        verify(userValidator).validateUpdate(1L, "testuser", "testuser", null, "test@test.com");
    }

    // === READ ===

    @Test
    void getById_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(responseMapper.apply(user)).thenReturn(responseDto);

        UserResponseDto result = userService.getById(1L);

        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getByPublicId_shouldReturnUser() {
        when(userRepository.findByPublicId(publicId)).thenReturn(Optional.of(user));
        when(responseMapper.apply(user)).thenReturn(responseDto);

        UserResponseDto result = userService.getByPublicId(publicId);

        assertThat(result.publicId()).isEqualTo(publicId);
    }

    @Test
    void getByUsername_shouldReturnUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(responseMapper.apply(user)).thenReturn(responseDto);

        UserResponseDto result = userService.getByUsername("testuser");

        assertThat(result.username()).isEqualTo("testuser");
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAll_shouldReturnPageResponse() {
        Pageable pageable = PageRequest.of(0, 20);
        UserSearchCriteria criteria = new UserSearchCriteria(null, null, null);
        Page<User> page = new PageImpl<>(List.of(user), pageable, 1);

        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(responseMapper.apply(user)).thenReturn(responseDto);

        PageResponse<UserResponseDto> result = userService.getAll(criteria, pageable);

        assertThat(result.content()).hasSize(1);
    }

    @Test
    @SuppressWarnings("unchecked")
    void findAll_shouldReturnList() {
        UserSearchCriteria criteria = new UserSearchCriteria(null, null, null);
        when(userRepository.findAll(any(Specification.class))).thenReturn(List.of(user));
        when(responseMapper.apply(user)).thenReturn(responseDto);

        List<UserResponseDto> result = userService.findAll(criteria);

        assertThat(result).hasSize(1);
    }

    // === STATUS ACTIONS ===

    @Test
    void activateUser_shouldSetStatusActive() {
        user.setStatus(UserStatus.SUSPENDED);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.activateUser(1L);

        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void suspendUser_shouldSetStatusSuspended() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.suspendUser(1L);

        assertThat(user.getStatus()).isEqualTo(UserStatus.SUSPENDED);
    }

    @Test
    void delete_shouldSoftDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.delete(1L);

        assertThat(user.getStatus()).isEqualTo(UserStatus.DELETED);
        assertThat(user.getDeletedAt()).isNotNull();
    }

    // === PROFILE ===

    @Test
    void updateProfile_shouldUpdateEmail() {
        UserProfileUpdateDto dto = new UserProfileUpdateDto("newemail@test.com");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        doNothing().when(userValidator).validateEmailUpdate(1L, "newemail@test.com", "test@test.com");
        when(userRepository.save(user)).thenReturn(user);
        when(responseMapper.apply(user)).thenReturn(responseDto);

        UserResponseDto result = userService.updateProfile("testuser", dto);

        assertThat(result).isNotNull();
        assertThat(user.getEmail()).isEqualTo("newemail@test.com");
    }

    @Test
    void updateProfile_shouldThrowWhenEmailTaken() {
        UserProfileUpdateDto dto = new UserProfileUpdateDto("taken@test.com");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        doThrow(new ResourceInUseException(it.andrea.insula.user.internal.user.exception.UserErrorCodes.EMAIL_ALREADY_EXISTS, "taken@test.com"))
                .when(userValidator).validateEmailUpdate(1L, "taken@test.com", "test@test.com");

        assertThatThrownBy(() -> userService.updateProfile("testuser", dto))
                .isInstanceOf(ResourceInUseException.class);
    }
}

