# Swagger/OpenAPI Documentation - Auth Service

## 📚 Acceso a la Documentación

Una vez que el Auth Service esté corriendo en el puerto 8088, puedes acceder a:

### Swagger UI (Interfaz Interactiva)
```
http://localhost:8088/api/swagger-ui.html
```

**Características:**
- ✅ Interfaz visual para probar todos los endpoints
- ✅ Ejemplos de request/response automáticos
- ✅ Autenticación JWT integrada (botón "Authorize")
- ✅ Schemas y modelos detallados
- ✅ Try it out! para ejecutar requests en vivo

### API Docs JSON (OpenAPI 3.0)
```
http://localhost:8088/api/api-docs
```

Formato JSON con la especificación completa OpenAPI que puede importarse en:
- Postman
- Insomnia
- Redocly
- API Platform de tu elección

---

## 🔐 Autenticación en Swagger UI

Para probar endpoints protegidos:

1. Ejecuta el endpoint `POST /auth/login` con credenciales válidas
2. Copia el `token` de la respuesta
3. Click en el botón **"Authorize"** (candado) en la parte superior
4. Pega el token en el campo (sin "Bearer ")
5. Click en "Authorize"
6. Ahora puedes probar endpoints protegidos

---

## 📡 Endpoints Documentados

### 1. POST `/auth/login`
**Descripción:** Autenticación de operador y generación de PIN temporal

**Tags:** `Authentication`

**Request Body:**
```json
{
  "username": "carlos.martinez",
  "password": "alwon2025"
}
```

**Responses:**
- **200 OK:** Login exitoso
  ```json
  {
    "success": true,
    "operator": {
      "id": "op123",
      "username": "carlos.martinez",
      "fullName": "Carlos Martínez",
      "email": "carlos@alwon.com",
      "phone": "+57 300 123 4567",
      "role": "OPERATOR"
    },
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "pin": "472915",
    "pinExpiresAt": "2025-12-25T20:30:00Z",
    "notifications": {
      "whatsapp": {
        "sent": true,
        "maskedPhone": "***-***-4567"
      },
      "email": {
        "sent": true,
        "maskedEmail": "c***@alwon.com"
      }
    }
  }
  ```
- **401 Unauthorized:** Credenciales inválidas
- **429 Too Many Requests:** Rate limit excedido (5/min)

---

### 2. POST `/auth/validate-pin`
**Descripción:** Valida PIN de 6 dígitos

**Tags:** `Authentication`

**Security:** Bearer JWT required

**Request Body:**
```json
{
  "pin": "472915"
}
```

**Responses:**
- **200 OK:** PIN válido
- **401 Unauthorized:** PIN incorrecto (con intentos restantes)
- **429 Too Many Requests:** Máximo de intentos alcanzado

---

### 3. POST `/auth/logout`
**Descripción:** Cierra sesión e invalida PIN

**Tags:** `Authentication`

**Security:** Bearer JWT required

**Responses:**
- **200 OK:** Sesión cerrada exitosamente

---

### 4. GET `/auth/session`
**Descripción:** Verifica sesión activa

**Tags:** `Authentication`

**Security:** Bearer JWT required

**Responses:**
- **200 OK:** Información de sesión activa
- **401 Unauthorized:** Sesión inválida o expirada

---

## 🎨 Anotaciones OpenAPI en Código

### Ejemplo: LoginController con anotaciones completas

```java
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints de autenticación de operadores")
@SecurityRequirement(name = "Bearer Authentication")
public class AuthController {

    @PostMapping("/login")
    @Operation(
        summary = "Login de operador",
        description = "Autentica un operador con usuario/contraseña y genera un PIN temporal de 6 dígitos. " +
                      "El PIN se envía automáticamente por WhatsApp y Email."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login exitoso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginResponse.class),
                examples = @ExampleObject(
                    value = """
                    {
                      "success": true,
                      "operator": {
                        "id": "op123",
                        "fullName": "Carlos Martínez"
                      },
                      "token": "eyJhbGciOiJIUzI1NiIs...",
                      "pin": "472915",
                      "pinExpiresAt": "2025-12-25T20:30:00Z"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Credenciales inválidas",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                    {
                      "success": false,
                      "error": "INVALID_CREDENTIALS",
                      "message": "Usuario o contraseña incorrectos"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "429",
            description = "Rate limit excedido (5 intentos por minuto)"
        )
    })
    @RateLimiter(name = "auth")
    public ResponseEntity<LoginResponse> login(
        @Valid @RequestBody 
        @Parameter(description = "Credenciales del operador") 
        LoginRequest request
    ) {
        // Implementation
    }
}
```

---

## 📊 Schemas y Modelos

Todos los DTOs deben tener anotaciones para documentación:

```java
@Schema(description = "Request para login de operador")
public class LoginRequest {
    
    @Schema(
        description = "Nombre de usuario del operador",
        example = "carlos.martinez",
        required = true,
        minLength = 4,
        maxLength = 50
    )
    @NotBlank
    @Size(min = 4, max = 50)
    private String username;
    
    @Schema(
        description = "Contraseña del operador",
        example = "alwon2025",
        required = true,
        minLength = 8
    )
    @NotBlank
    @Size(min = 8)
    private String password;
}
```

---

## 🚀 Cómo Probar en Desarrollo

### Paso 1: Iniciar Auth Service
```bash
cd backend/auth-service
mvn spring-boot:run
```

### Paso 2: Abrir Swagger UI
```
http://localhost:8088/api/swagger-ui.html
```

### Paso 3: Probar Login
1. Expandir endpoint `POST /auth/login`
2. Click en "Try it out"
3. Usar credenciales de prueba:
   ```json
   {
     "username": "carlos.martinez",
     "password": "alwon2025"
   }
   ```
4. Click en "Execute"
5. Ver respuesta con token y PIN

### Paso 4: Autorizar con JWT
1. Copiar el `token` de la respuesta
2. Click en "Authorize" (🔒 arriba a la derecha)
3. Pegar token
4. Click en "Authorize"

### Paso 5: Probar endpoints protegidos
Ahora puedes probar `/validate-pin`, `/logout`, `/session`

---

## 📦 Exportar Documentación

### Para Postman
1. Ir a `http://localhost:8088/api/api-docs`
2. Copiar JSON completo
3. En Postman: Import → Raw text → Pegar JSON
4. ✅ Collection completa importada

### Para README.md
Puedes usar herramientas como:
- `swagger-markdown` (npm)
- `openapi-generator` (Java)
- `redoc-cli` (npm)

---

## 🎯 Siguiente Paso

Con Swagger configurado, cuando implementes los controllers completos, la documentación se generará automáticamente con solo agregar las anotaciones `@Operation`, `@ApiResponse`, etc.

**Estado actual:**
- ✅ Dependencia agregada (springdoc-openapi)
- ✅ Configuración en application.properties
- ✅ OpenAPIConfig.java creado
- ⏳ Pendiente: Agregar anotaciones a controllers

**URL Final:**
```
http://localhost:8088/api/swagger-ui.html
```
