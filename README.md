# Alwon Kiosk - Backend

Sistema backend para el Kiosk POS de Alwon, construido con Java Spring Boot.

## 🚀 Tecnologías

- **Java 17**
- **Spring Boot 3.x**
- **Maven**
- **H2 Database** (desarrollo)
- **PostgreSQL** (producción)

## 📁 Estructura del Proyecto

```
backend/
├── src/main/java/com/alwon/kiosk/
│   ├── model/         # Entidades JPA
│   ├── dto/           # Data Transfer Objects
│   ├── repository/    # Repositorios JPA
│   ├── service/       # Lógica de negocio
│   ├── controller/    # Controladores REST
│   └── config/        # Configuración
├── src/main/resources/
│   └── application.properties
└── pom.xml
```

## 🔧 Configuración

### Requisitos
- Java 17+
- Maven 3.6+

### Instalación

```bash
# Clonar el repositorio
git clone https://github.com/TU_USUARIO/alwon-kiosk-backend.git
cd alwon-kiosk-backend

# Compilar
mvn clean install

# Ejecutar
mvn spring-boot:run
```

El servidor estará disponible en: `http://localhost:8080`

## 📡 API Endpoints

### Sesiones de Cliente
- `POST /api/kiosk/customer-session` - Crear sesión
- `GET /api/kiosk/session/{id}` - Obtener sesión

### Carrito
- `PATCH /api/kiosk/session/{id}/cart` - Modificar carrito (requiere PIN de staff)

### Pagos
- `POST /api/kiosk/session/{id}/payment` - Procesar pago

### Cancelación
- `DELETE /api/kiosk/session/{id}/cancel` - Cancelar transacción

Ver [API_DOCUMENTATION.md](docs/API_DOCUMENTATION.md) para detalles completos.

## ⚙️ Configuración

Editar `src/main/resources/application.properties`:

```properties
# Puerto del servidor
server.port=8080

# Base de datos (H2 por defecto)
spring.datasource.url=jdbc:h2:mem:kioskdb

# PIN de staff (cambiar en producción)
kiosk.staff.pin=1234

# Expiración de sesión (minutos)
kiosk.session.expiration-minutes=10
```

## 🔒 Seguridad

- Autenticación de staff mediante PIN
- CORS configurado para desarrollo
- **IMPORTANTE**: Cambiar `kiosk.staff.pin` en producción
- Usar HTTPS en producción

## 📝 Licencia

Propietario - Alwon © 2024
