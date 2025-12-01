package koval.proxyseller.twitter.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Value('${openapi.title:Twitter API}')
    private String title

    @Value('${openapi.version:1.0.0}')
    private String version

    @Value('${openapi.description:RESTful API for Twitter-like social media application}')
    private String description

    @Value('${openapi.contact.name:API Support}')
    private String contactName

    @Value('${openapi.contact.email:support@twitter.com}')
    private String contactEmail

    @Value('${openapi.license.name:Apache 2.0}')
    private String licenseName

    @Value('${openapi.license.url:https://www.apache.org/licenses/LICENSE-2.0.html}')
    private String licenseUrl

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .version(version)
                        .description(description)
                        .contact(new Contact()
                                .name(contactName)
                                .email(contactEmail))
                        .license(new License()
                                .name(licenseName)
                                .url(licenseUrl)))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
    }
}

