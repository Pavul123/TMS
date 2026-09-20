package com.transport.tms.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("TransFlow TMS — Enterprise Backend REST API")
                        .version("1.0.0")
                        .description("REST API Documentation & Interactive Testing Console for TransFlow Transport Management System.<br>" +
                                "<b>Authentication:</b> Click the green <b>'Authorize'</b> button and enter your JWT Bearer token obtained from <code>/api/v1/auth/login</code>.<br>" +
                                "<b>Demo Accounts:</b> <code>admin/admin123</code> (Admin), <code>md/md123</code> (MD), <code>manager/manager123</code> (Manager), <code>accounts/accounts123</code> (Accounts), <code>worker/worker123</code> (Worker).")
                        .contact(new Contact()
                                .name("TransFlow Engineering Team")
                                .email("engineering@transflow.internal"))
                        .license(new License().name("Proprietary - TransFlow Enterprise")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT token (without 'Bearer ' prefix)")));
    }
}
