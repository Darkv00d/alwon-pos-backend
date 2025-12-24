# Guía de Compilación y Despliegue - Alwon POS Backend

## 📋 Requisitos Previos

- Java 21 JDK
- Maven 3.8+
- Docker y Docker Compose
- PostgreSQL 15 (si se ejecuta localmente)

## 🔧 Compilación Individual

Para compilar cada microservicio individualmente:

```bash
cd backend/<service-name>
mvn clean package -DskipTests
```

## 🏗️ Compilación Completa

Para compilar todos los servicios:

```bash
# Desde la raíz del proyecto
cd backend

# Compilar todos los servicios
for dir in api-gateway session-service cart-service product-service payment-service camera-service access-service inventory-service websocket-server; do
  echo "Building $dir..."
  cd $dir
  mvn clean package -DskipTests
  cd ..
done
```

## 🐳 Despliegue con Docker Compose

### 1. Compilar Imágenes Docker

```bash
# Desde la raíz del proyecto
docker-compose build
```

### 2. Iniciar Todos los Servicios

```bash
docker-compose up -d
```

### 3. Verificar Estado

```bash
docker-compose ps
```

### 4. Ver Logs

```bash
# Todos los servicios
docker-compose logs -f

# Servicio específico
docker-compose logs -f <service-name>
```

## 🎯 Endpoints de Verificación

Una vez iniciados, puedes verificar cada servicio:

| Servicio | Puerto | Health Check |
|----------|--------|--------------|
| API Gateway | 8080 | http://localhost:8080/actuator/health |
| Session Service | 8081 | http://localhost:8081/health |
| Cart Service | 8082 | http://localhost:8082/health |
| Product Service | 8083 | http://localhost:8083/health |
| Payment Service | 8084 | http://localhost:8084/health |
| Camera Service | 8085 | http://localhost:8085/health |
| Access Service | 8086 | http://localhost:8086/health |
| Inventory Service | 8087 | http://localhost:8087/health |
| WebSocket Server | 8090 | http://localhost:8090/actuator/health |

## 📚 Swagger UI

Cada servicio expone su documentación API:

- **Session**: http://localhost:8081/swagger-ui.html
- **Cart**: http://localhost:8082/swagger-ui.html
- **Product**: http://localhost:8083/swagger-ui.html
- **Payment**: http://localhost:8084/swagger-ui.html
- **Camera**: http://localhost:8085/swagger-ui.html
- **Access**: http://localhost:8086/swagger-ui.html
- **Inventory**: http://localhost:8087/swagger-ui.html

## 🔄 Comandos Útiles

```bash
# Detener todos los servicios
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v

# Reiniciar un servicio específico
docker-compose restart <service-name>

# Reconstruir y reiniciar
docker-compose up -d --build <service-name>
```

## 🗄️ Base de Datos

- **PostgreSQL**: localhost:5432
- **Database**: alwon_pos
- **Usuario**: alwon
- **Password**: alwon2024

### Conexión:
```bash
psql -h localhost -U alwon -d alwon_pos
```

## 📨 RabbitMQ

- **AMQP Port**: 5672
- **Management UI**: http://localhost:15672
- **Usuario**: alwon
- **Password**: alwon2024

## 🚀 Orden de Inicio Recomendado

1. PostgreSQL
2. RabbitMQ
3. Session Service
4. Product Service
5. Cart Service
6. Payment Service
7. Camera Service
8. Access Service
9. Inventory Service
10. WebSocket Server
11. API Gateway

Docker Compose maneja este orden automáticamente mediante `depends_on`.
