package it.andrea.insula.user.internal.permission.mapper;

import it.andrea.insula.core.dto.TranslatedEnum;
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

    private final MessageSource messageSource;

    @Override
    public PermissionResponseDto apply(Permission permission) {
        Locale locale = LocaleContextHolder.getLocale();

        TranslatedEnum domain = translateDomain(permission.getDomain(), locale);
        String description = translateDescription(permission.getAuthority(), permission.getDescription(), locale);

        return new PermissionResponseDto(
                permission.getId(),
                permission.getAuthority(),
                description,
                domain
        );
    }

    private TranslatedEnum translateDomain(String domainCode, Locale locale) {
        String code = "enum.domain." + domainCode;
        String label = messageSource.getMessage(code, null, domainCode, locale);
        return new TranslatedEnum(domainCode, label);
    }

    private String translateDescription(String authority, String defaultDescription, Locale locale) {
        String code = "permission.description." + authority;
        return messageSource.getMessage(code, null, defaultDescription, locale);
    }
}
