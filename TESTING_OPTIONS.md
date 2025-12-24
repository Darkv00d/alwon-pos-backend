# Testing Quick Guide - Opciones Disponibles

## ⚠️ Situación Actual

Docker Desktop **no está corriendo** en el sistema.

## 🎯 Opciones para Testing

### Opción 1: Docker Compose (Recomendado) ⭐

**Pasos:**
1. Iniciar Docker Desktop manualmente
2. Esperar a que cargue completamente  
3. Ejecutar:
```bash
cd C:\Users\algam\.gemini\antigravity\scratch\Alwon\POS
docker-compose up -d postgres rabbitmq
docker-compose up -d
```

**Ventajas:**
- ✅ Todo en contenedores aislados
- ✅ Fácil de limpiar
- ✅ Configuración predefinida
- ✅ Más cercano a producción

**Tiempo:** ~5min (inicio Docker) + 3min (build images)

---

### Opción 2: Maven Individual (Más Rápido)

**Requisitos:**
- PostgreSQL local corriendo en puerto 5432
- RabbitMQ opcional (solo para WebSocket)

**Pasos:**
```bash
# Terminal 1: API Gateway
cd backend/api-gateway
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.9.10-hotspot"
mvn spring-boot:run

# Terminal 2: Session Service
cd backend/session-service
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.9.10-hotspot"
mvn spring-boot:run

# Terminal 3: Product Service
cd backend/product-service
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.9.10-hotspot"
mvn spring-boot:run
```

**Ventajas:**
- ✅ Inicio inmediato
- ✅ Logs visibles directamente
- ✅ Fácil debugging

**Tiempo:** ~2min por servicio

---

### Opción 3: Testing Básico API Gateway

**Sin Base de Datos - Solo verificación de rutas**

```bash
cd backend/api-gateway
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.9.10-hotspot"
mvn spring-boot:run
```

**Pruebas:**
```bash
# Health check
curl http://localhost:8080/actuator/health

# Gateway routes
curl http://localhost:8080/actuator/gateway/routes
```

**Ventajas:**
- ✅ Más rápido
- ✅ Sin dependencias

**Tiempo:** ~1min

---

## 🔍 Verificar PostgreSQL Local

```powershell
# Ver si está corriendo
Test-NetConnection -ComputerName localhost -Port 5432

# O con psql
psql -h localhost -U alwon -d alwon_pos
```

## 📝 Siguiente Paso

**Recomendación:** Opción 2 si tienes PostgreSQL local, sino Opción 1.

Ejecuta el comando correspondiente y luego probamos endpoints.
