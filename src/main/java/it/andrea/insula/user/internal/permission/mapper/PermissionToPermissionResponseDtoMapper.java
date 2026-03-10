package it.andrea.insula.user.internal.permission.mapper;

import it.andrea.insula.core.dto.EnumTranslator;
import it.andrea.insula.user.internal.permission.dto.response.PermissionResponseDto;
import it.andrea.insula.user.internal.permission.model.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class PermissionToPermissionResponseDtoMapper implements Function<Permission, PermissionResponseDto> {

    private static final String DOMAIN_PREFIX = "enum.domain.";
    private static final String DESCRIPTION_PREFIX = "permission.description.";

    private final MessageSource messageSource;
    private final EnumTranslator enumTranslator;

    @Override
    public PermissionResponseDto apply(Permission permission) {
        Locale locale = LocaleContextHolder.getLocale();

        return new PermissionResponseDto(
                permission.getId(),
                permission.getAuthority(),
                translateDescription(permission.getAuthority(), permission.getDescription(), locale),
                enumTranslator.translate(DOMAIN_PREFIX, permission.getDomain())
        );
    }

    private String translateDescription(String authority, String defaultDescription, Locale locale) {
        String code = DESCRIPTION_PREFIX + authority;
        return messageSource.getMessage(code, null, defaultDescription, locale);
    }
}
