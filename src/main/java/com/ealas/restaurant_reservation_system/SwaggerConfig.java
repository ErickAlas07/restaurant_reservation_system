package com.ealas.restaurant_reservation_system;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Java - e-commerce.")
                        .description("API para el sistema de reservas de restaurante.")
                        .version("1.0")
                        .contact(new Contact().name("Erick Galdámez")
                                .email("erick777gal@gmail.com")
                                .url("https://github.com/ErickAlas07"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}