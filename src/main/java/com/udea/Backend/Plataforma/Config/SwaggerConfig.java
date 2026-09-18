package com.udea.Backend.Plataforma.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Plataforma de Reservas de Servicios")
                        .version("1.0.0")
                        .description("Documentación oficial de la API REST para el Backend (Fábrica Escuela).")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo")
                                .email("soporte@udea.edu.co")));
    }
}
