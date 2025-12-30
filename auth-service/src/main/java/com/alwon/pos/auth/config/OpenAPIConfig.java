package com.alwon.pos.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger Configuration for Auth Service
 * 
 * Access documentation at: http://localhost:8088/api/swagger-ui.html
 * API Docs JSON: http://localhost:8088/api/api-docs
 */
@Configuration
public class OpenAPIConfig {

    @Value("${server.port:8088}")
    private int serverPort;

    @Bean
    public OpenAPI authServiceOpenAPI() {
        // Server configuration
        Server localServer = new Server()
                .url("http://localhost:" + serverPort + "/api")
                .description("Local Development Server");

        Server devServer = new Server()
                .url("https://dev-api.alwon.com/auth")
                .description("Development Server");

        Server prodServer = new Server()
                .url("https://api.alwon.com/auth")
                .description("Production Server");

        // Contact information
        Contact contact = new Contact()
                .name("Alwon POS Team")
                .email("dev@alwon.com")
                .url("https://alwon.com");

        // License
        License license = new License()
                .name("Proprietary")
                .url("https://alwon.com/license");

        // API Info
        Info info = new Info()
                .title("Alwon POS - Auth Service API")
                .version("1.0.0")
                .description("""
                        # Alwon POS Authentication Service

                        Sistema de autenticación de operadores con PIN temporal y notificaciones.

                        ## Características:
                        - 🔐 Login con usuario/contraseña validado contra sistema central
                        - 🔢 Generación automática de PIN temporal de 6 dígitos
                        - 📱 Notificaciones por WhatsApp (Twilio)
                        - 📧 Notificaciones por Email (SendGrid)
                        - 🔑 Tokens JWT para sesiones
                        - 🔴 Almacenamiento seguro de PIN en Redis (TTL 8h)
                        - 🛡️ Rate limiting (5 intentos/minuto)
                        - 📝 Audit log completo de acciones

                        ## Flujo de Autenticación:
                        1. **Login** → Genera PIN y envía notificaciones
                        2. **PIN Display** → Frontend muestra PIN por 5 segundos
                        3. **PIN Validation** → Máximo 3 intentos
                        4. **Admin Access** → Cierre del día, ventas, pérdidas

                        ## Seguridad:
                        - Passwords hasheados con BCrypt (costo 10)
                        - PINs hasheados en Redis con TTL automático
                        - JWT tokens con expiración de 8 horas
                        - Blacklist de tokens revocados
                        """)
                .contact(contact)
                .license(license);

        // Security scheme for JWT
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT token obtenido del endpoint /auth/login");

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("Bearer Authentication");

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer, devServer, prodServer))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", securityScheme))
                .addSecurityItem(securityRequirement);
    }
}
