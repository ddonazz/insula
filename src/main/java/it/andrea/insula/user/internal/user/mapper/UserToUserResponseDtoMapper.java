package it.andrea.insula.user.internal.user.mapper;

import it.andrea.insula.core.dto.EnumTranslator;
import it.andrea.insula.user.internal.role.mapper.RoleToRoleResponseDtoMapper;
import it.andrea.insula.user.internal.user.dto.response.UserResponseDto;
import it.andrea.insula.user.internal.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserToUserResponseDtoMapper implements Function<User, UserResponseDto> {

    private final RoleToRoleResponseDtoMapper roleToRoleResponseDtoMapper;
    private final EnumTranslator enumTranslator;

    @Override
    public UserResponseDto apply(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .publicId(user.getPublicId())
                .username(user.getUsername())
                .email(user.getEmail())
                .status(enumTranslator.translate(user.getStatus()))
                .roles(user.getRoles()
                        .stream()
                        .map(roleToRoleResponseDtoMapper)
                        .collect(Collectors.toSet()))
                .build();
    }
}
