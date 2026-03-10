package it.andrea.insula.core.locale;

import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

/**
 * Thread-local holder for the current request locale.
 * <p>
 * Works in tandem with Spring's {@link LocaleContextHolder} to guarantee
 * that the locale resolved from the {@code Accept-Language} header is available
 * throughout the entire request lifecycle, including in non-servlet layers
 * (services, mappers, async tasks).
 */
public class LocaleContext {

    private static final ThreadLocal<Locale> CURRENT_LOCALE = new ThreadLocal<>();

    public static Locale getLocale() {
        Locale locale = CURRENT_LOCALE.get();
        return locale != null ? locale : Locale.ITALIAN;
    }

    public static void setLocale(Locale locale) {
        CURRENT_LOCALE.set(locale);
        LocaleContextHolder.setLocale(locale);
    }

    public static void clear() {
        CURRENT_LOCALE.remove();
        LocaleContextHolder.resetLocaleContext();
    }
}

