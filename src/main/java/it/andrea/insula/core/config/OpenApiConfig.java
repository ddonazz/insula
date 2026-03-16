package it.andrea.insula.core.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${insula.openapi.title}")
    private String title;

    @Value("${insula.openapi.version}")
    private String version;

    @Value("${insula.openapi.description}")
    private String description;

    @Value("${insula.openapi.contact.name}")
    private String contactName;

    @Value("${insula.openapi.contact.email}")
    private String contactEmail;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .version(version)
                        .description(description)
                        .contact(new Contact().name(contactName).email(contactEmail))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    @Bean
    public GroupedOpenApi userDomainApi() {
        return GroupedOpenApi.builder()
                .group("Modulo Utenti")
                .packagesToScan("it.andrea.insula.user")
                .build();
    }

    @Bean
    public GroupedOpenApi agencyDomainApi() {
        return GroupedOpenApi.builder()
                .group("Modulo Agenzie")
                .packagesToScan("it.andrea.insula.agency")
                .build();
    }

    @Bean
    public GroupedOpenApi customerDomainApi() {
        return GroupedOpenApi.builder()
                .group("Modulo Clienti")
                .packagesToScan("it.andrea.insula.customer")
                .build();
    }

    @Bean
    public GroupedOpenApi ownerDomainApi() {
        return GroupedOpenApi.builder()
                .group("Modulo Proprietari")
                .packagesToScan("it.andrea.insula.owner")
                .build();
    }

    @Bean
    public GroupedOpenApi pricingDomainApi() {
        return GroupedOpenApi.builder()
                .group("Modulo Tariffe")
                .packagesToScan("it.andrea.insula.pricing")
                .build();
    }

    @Bean
    public GroupedOpenApi propertyDomainApi() {
        return GroupedOpenApi.builder()
                .group("Modulo Proprietà")
                .packagesToScan("it.andrea.insula.property")
                .build();
    }
}