package com.coderhouse.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {
	
	// 
    private static final String TITLE = "API REST Full · Java · CoderHouse";
    private static final String VERSION = "3.0.0";
    private static final String DESCRIPTION = """
            La API REST proporciona un conjunto de endpoints diseñados para gestionar eficientemente clientes, 
            productos y ventas en una plataforma de comercio electrónico.
            Con esta API, puedes realizar operaciones CRUD (Crear, Leer, Actualizar y Eliminar) en cada uno de 
            los modelos disponibles, lo que permite mantener los datos siempre actualizados.
            
            Las principales funcionalidades incluyen:
            
            · Listar, agregar, editar y eliminar clientes, productos y registros de ventas.
            
            · Facilitar la administración de información clave para el correcto funcionamiento de la plataforma.
            
            · Garantizar la integración con sistemas externos mediante un diseño escalable y flexible.

            Esta API está completamente documentada con Swagger/OpenAPI, lo que permite a desarrolladores explorar, 
            comprender y probar los endpoints de manera sencilla. Ideal para optimizar el desarrollo y garantizar 
            la máxima eficiencia en tus proyectos.
            """;
    private static final String TERMS_OF_SERVICE = "https://www.coderhouse.com/terms";
    private static final String CONTACT_NAME = "Valeria Casatti";
    private static final String CONTACT_EMAIL = "valeria.casatti@gmail.com";
    private static final String CONTACT_URL = "https://github.com/valeriacasatti/FacturacionEntregaProyectoFinal-Casatti";
    private static final String LICENSE_NAME = "License";
    private static final String LICENSE_URL = "https://opensource.org/licenses";
    private static final String LOCAL_SERVER_URL = "http://localhost:8080";
    private static final String LOCAL_SERVER_DESCRIPTION = "Servidor Local";
    private static final String PROD_SERVER_URL = "https://api.coderhouse.com";
    private static final String PROD_SERVER_DESCRIPTION = "Servidor de Producción";

    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(TITLE)
                        .version(VERSION)
                        .description(DESCRIPTION)
                        .termsOfService(TERMS_OF_SERVICE)
                        .contact(new Contact()
                                .name(CONTACT_NAME)
                                .email(CONTACT_EMAIL)
                                .url(CONTACT_URL))
                        .license(new License()
                                .name(LICENSE_NAME)
                                .url(LICENSE_URL)))
                .servers(List.of(
                        new Server()
                                .url(LOCAL_SERVER_URL)
                                .description(LOCAL_SERVER_DESCRIPTION),
                        new Server()
                                .url(PROD_SERVER_URL)
                                .description(PROD_SERVER_DESCRIPTION)));
    }
}
