package it.andrea.insula.core.dto;

import it.andrea.insula.core.locale.LocaleContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Centralised utility for translating enums into {@link TranslatedEnum} DTOs.
 * <p>
 * Convention for message keys: {@code enum.<enumclasslowercase>.<VALUE>}
 * <p>
 * Example: enum {@code PropertyType.BUILDING} → key {@code enum.propertytype.BUILDING}
 */
@Component
@RequiredArgsConstructor
public class EnumTranslator {

    private final MessageSource messageSource;

    /**
     * Translates a single enum value using the current locale.
     *
     * @param value the enum constant (may be {@code null})
     * @param <E>   enum type
     * @return a {@link TranslatedEnum} or {@code null} if value is {@code null}
     */
    public <E extends Enum<E>> TranslatedEnum translate(E value) {
        if (value == null) {
            return null;
        }
        Locale locale = LocaleContext.getLocale();
        String key = buildKey(value);
        String label = messageSource.getMessage(key, null, value.name(), locale);
        return new TranslatedEnum(value.name(), label);
    }

    /**
     * Translates a set of enum values using the current locale.
     *
     * @param values the set of enum constants (may be {@code null} or empty)
     * @param <E>    enum type
     * @return a set of {@link TranslatedEnum}, never {@code null}
     */
    public <E extends Enum<E>> Set<TranslatedEnum> translateAll(Set<E> values) {
        if (values == null || values.isEmpty()) {
            return Collections.emptySet();
        }
        return values.stream()
                .map(this::translate)
                .collect(Collectors.toSet());
    }

    /**
     * Translates a raw string code (e.g. a domain code) with an explicit prefix.
     *
     * @param prefix the message-key prefix, e.g. {@code "enum.domain."}
     * @param code   the raw code value
     * @return a {@link TranslatedEnum} or {@code null} if code is {@code null}
     */
    public TranslatedEnum translate(String prefix, String code) {
        if (code == null) {
            return null;
        }
        Locale locale = LocaleContext.getLocale();
        String key = prefix + code;
        String label = messageSource.getMessage(key, null, code, locale);
        return new TranslatedEnum(code, label);
    }

    /**
     * Builds the message key for an enum value following the convention:
     * {@code enum.<lowercasesimpleclassname>.<NAME>}
     */
    private <E extends Enum<E>> String buildKey(E value) {
        return "enum." + value.getClass().getSimpleName().toLowerCase() + "." + value.name();
    }
}

