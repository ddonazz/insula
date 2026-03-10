package it.andrea.insula.core.locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

class LocaleContextTest {

    @AfterEach
    void tearDown() {
        LocaleContext.clear();
    }

    @Test
    void shouldReturnItalianWhenNoLocaleSet() {
        assertThat(LocaleContext.getLocale()).isEqualTo(Locale.ITALIAN);
    }

    @Test
    void shouldSetAndGetLocale() {
        LocaleContext.setLocale(Locale.ENGLISH);

        assertThat(LocaleContext.getLocale()).isEqualTo(Locale.ENGLISH);
    }

    @Test
    void shouldSyncWithLocaleContextHolder() {
        LocaleContext.setLocale(Locale.FRENCH);

        assertThat(LocaleContextHolder.getLocale()).isEqualTo(Locale.FRENCH);
    }

    @Test
    void shouldClearLocale() {
        LocaleContext.setLocale(Locale.GERMAN);
        LocaleContext.clear();

        assertThat(LocaleContext.getLocale()).isEqualTo(Locale.ITALIAN);
    }

    @Test
    void shouldIsolateLocalePerThread() throws InterruptedException {
        LocaleContext.setLocale(Locale.ENGLISH);

        Thread otherThread = new Thread(() -> {
            LocaleContext.setLocale(Locale.FRENCH);
            assertThat(LocaleContext.getLocale()).isEqualTo(Locale.FRENCH);
            LocaleContext.clear();
        });
        otherThread.start();
        otherThread.join();

        assertThat(LocaleContext.getLocale()).isEqualTo(Locale.ENGLISH);
    }

    @Test
    void shouldAllowOverwritingLocale() {
        LocaleContext.setLocale(Locale.ENGLISH);
        assertThat(LocaleContext.getLocale()).isEqualTo(Locale.ENGLISH);

        LocaleContext.setLocale(Locale.GERMAN);
        assertThat(LocaleContext.getLocale()).isEqualTo(Locale.GERMAN);
    }
}

