package com.banco.banquito.general.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BanQuito - Microservicio General")
                        .version("1.0.0")
                        .description("API para la gestión de cuentas bancarias y movimientos del sistema BanQuito")
                        .contact(new Contact()
                                .name("Equipo BanQuito")
                                .email("desarrollo@banquito.com")
                                .url("https://banquito.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addServersItem(new Server()
                        .url("http://localhost:8080")
                        .description("Servidor de Desarrollo"))
                .addServersItem(new Server()
                        .url("https://api.banquito.com")
                        .description("Servidor de Producción"));
    }
} 