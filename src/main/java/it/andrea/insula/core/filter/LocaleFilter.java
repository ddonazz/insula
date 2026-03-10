package it.andrea.insula.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.LocaleResolver;

import java.io.IOException;
import java.util.Locale;

/**
 * Filtro che risolve il locale dalla request (header Accept-Language)
 * e lo popola nel {@link LocaleContextHolder} all'inizio della filter chain.
 * <p>
 * Questo garantisce che il locale sia disponibile ovunque,
 * anche nei filtri di sicurezza e nelle risposte di errore
 * generate prima del DispatcherServlet.
 */
@RequiredArgsConstructor
public class LocaleFilter extends OncePerRequestFilter {

    private final LocaleResolver localeResolver;


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        Locale locale = localeResolver.resolveLocale(request);
        LocaleContextHolder.setLocale(locale);
        try {
            filterChain.doFilter(request, response);
        } finally {
            LocaleContextHolder.resetLocaleContext();
        }
    }
}

