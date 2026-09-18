package com.udea.Backend.Plataforma.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
                                .email("soporte@udea.edu.co")))
                // Agregar el requerimiento de seguridad global
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                // Configurar el esquema (Bearer JWT)
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }
}
