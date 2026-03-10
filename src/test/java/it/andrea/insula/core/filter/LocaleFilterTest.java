package it.andrea.insula.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LocaleFilterTest {

    @Mock
    private FilterChain filterChain;

    private LocaleFilter localeFilter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.ITALIAN);
        resolver.setSupportedLocales(List.of(Locale.ITALIAN, Locale.ENGLISH));

        localeFilter = new LocaleFilter(resolver);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @AfterEach
    void tearDown() {
        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void shouldSetItalianLocaleByDefault() throws ServletException, IOException {
        final Locale[] capturedLocale = new Locale[1];
        doAnswer(invocation -> {
            capturedLocale[0] = LocaleContextHolder.getLocale();
            return null;
        }).when(filterChain).doFilter(request, response);

        localeFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(capturedLocale[0]).isEqualTo(Locale.ITALIAN);
    }

    @Test
    void shouldSetEnglishLocaleFromAcceptLanguageHeader() throws ServletException, IOException {
        request.addHeader("Accept-Language", "en");

        final Locale[] capturedLocale = new Locale[1];
        doAnswer(invocation -> {
            capturedLocale[0] = LocaleContextHolder.getLocale();
            return null;
        }).when(filterChain).doFilter(request, response);

        localeFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(capturedLocale[0]).isEqualTo(Locale.ENGLISH);
    }

    @Test
    void shouldSetItalianLocaleFromAcceptLanguageHeader() throws ServletException, IOException {
        request.addHeader("Accept-Language", "it");

        final Locale[] capturedLocale = new Locale[1];
        doAnswer(invocation -> {
            capturedLocale[0] = LocaleContextHolder.getLocale();
            return null;
        }).when(filterChain).doFilter(request, response);

        localeFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(capturedLocale[0]).isEqualTo(Locale.ITALIAN);
    }

    @Test
    void shouldCleanupLocaleContextAfterFilter() throws ServletException, IOException {
        request.addHeader("Accept-Language", "en");

        localeFilter.doFilterInternal(request, response, filterChain);

        // Dopo il filtro il LocaleContextHolder deve essere stato resettato
        // (torna al default del JVM o del setDefaultLocale)
        assertThat(LocaleContextHolder.getLocale()).isNotEqualTo(Locale.ENGLISH);
    }

    @Test
    void shouldFallbackToDefaultForUnsupportedLocale() throws ServletException, IOException {
        request.addHeader("Accept-Language", "fr");

        final Locale[] capturedLocale = new Locale[1];
        doAnswer(invocation -> {
            capturedLocale[0] = LocaleContextHolder.getLocale();
            return null;
        }).when(filterChain).doFilter(request, response);

        localeFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        // Locale non supportato → fallback a italiano (default)
        assertThat(capturedLocale[0]).isEqualTo(Locale.ITALIAN);
    }
}

