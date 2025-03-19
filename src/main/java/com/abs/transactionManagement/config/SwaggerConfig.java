package com.abs.transactionManagement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.SpecVersion;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI myOpenAPI() {

        Contact contact = new Contact();
        contact.setEmail("itaccounts@coronationmb.com");
        contact.setName("CORONATION-MB");
        contact.setUrl("https://www.coronationmb.com");

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("ABS Transaction Management API")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints for CMB ABS Transaction Management.")
                .termsOfService("https://www.coronationmb.com/terms")
                .license(mitLicense);

        return new OpenAPI()
                .info(info)
                .specVersion(SpecVersion.V30);
    }
}
