package it.andrea.insula.core.config;

import it.andrea.insula.core.filter.LocaleFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

@Configuration
public class I18nConfig {

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.ITALIAN);
        resolver.setSupportedLocales(List.of(Locale.ITALIAN, Locale.ENGLISH));

        LocaleContextHolder.setDefaultLocale(Locale.ITALIAN);

        return resolver;
    }

    @Bean
    public FilterRegistrationBean<LocaleFilter> localeFilterRegistration(LocaleResolver localeResolver) {
        FilterRegistrationBean<LocaleFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new LocaleFilter(localeResolver));
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    @Bean
    public LocalValidatorFactoryBean getValidator(MessageSource messageSource) {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);
        return bean;
    }
}
