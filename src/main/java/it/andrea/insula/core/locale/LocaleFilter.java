package it.andrea.insula.core.locale;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

/**
 * Servlet filter that resolves the locale from the {@code Accept-Language} header
 * and populates {@link LocaleContext} for the duration of the request.
 * <p>
 * Runs at the highest priority so that the locale is available to every downstream
 * component (security filters, controllers, services, exception handlers).
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LocaleFilter extends OncePerRequestFilter {

    private static final Locale DEFAULT_LOCALE = Locale.ITALIAN;
    private static final Set<String> SUPPORTED_LANGUAGES = Set.of("it", "en");

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            LocaleContext.setLocale(resolveLocale(request));
            filterChain.doFilter(request, response);
        } finally {
            LocaleContext.clear();
        }
    }

    private Locale resolveLocale(HttpServletRequest request) {
        String acceptLanguage = request.getHeader("Accept-Language");
        if (acceptLanguage != null && !acceptLanguage.isBlank()) {
            String tag = acceptLanguage.split(",")[0].split(";")[0].trim();
            Locale locale = Locale.forLanguageTag(tag);
            if (SUPPORTED_LANGUAGES.contains(locale.getLanguage())) {
                return locale;
            }
        }
        return DEFAULT_LOCALE;
    }
}

