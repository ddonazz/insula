package it.andrea.insula.user.internal.user.mapper;

import it.andrea.insula.core.dto.TranslatedEnum;
import it.andrea.insula.user.internal.role.mapper.RoleToRoleResponseDtoMapper;
import it.andrea.insula.user.internal.user.dto.response.UserResponseDto;
import it.andrea.insula.user.internal.user.model.User;
import it.andrea.insula.user.internal.user.model.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserToUserResponseDtoMapper implements Function<User, UserResponseDto> {

    private final RoleToRoleResponseDtoMapper roleToRoleResponseDtoMapper;
    private final MessageSource messageSource;

    @Override
    public UserResponseDto apply(User user) {
        Locale locale = LocaleContextHolder.getLocale();

        return UserResponseDto.builder()
                .id(user.getId())
                .publicId(user.getPublicId())
                .username(user.getUsername())
                .email(user.getEmail())
                .status(translateStatus(user.getStatus(), locale))
                .roles(user.getRoles()
                        .stream()
                        .map(roleToRoleResponseDtoMapper)
                        .collect(Collectors.toSet()))
                .build();
    }

    private TranslatedEnum translateStatus(UserStatus status, Locale locale) {
        if (status == null) {
            return null;
        }
        String code = "enum.userstatus." + status.name();
        String label = messageSource.getMessage(code, null, status.name(), locale);
        return new TranslatedEnum(status.name(), label);
    }
}
