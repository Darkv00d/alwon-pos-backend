# Docker Compose Testing - Comandos

## Paso 1: Iniciar Infraestructura
```powershell
cd C:\Users\algam\.gemini\antigravity\scratch\Alwon\POS
docker-compose up -d postgres rabbitmq
```

## Paso 2: Verificar que iniciaron
```powershell
docker-compose ps
```

Deberías ver:
- alwon-postgres: Up (healthy)
- alwon-rabbitmq: Up (healthy)

## Paso 3: Ver logs si hay problemas
```powershell
docker-compose logs postgres
docker-compose logs rabbitmq
```

## Paso 4: Iniciar servicios compilados
```powershell
# Solo los 7 que compilaron exitosamente
docker-compose up -d api-gateway session-service cart-service product-service access-service inventory-service websocket-server
```

## Paso 5: Verificar servicios
```powershell
# Script PowerShell
.\backend\verify-services.ps1
```

## Paso 6: Probar endpoints manualmente

### Health Checks
```powershell
curl http://localhost:8080/actuator/health
curl http://localhost:8081/health
curl http://localhost:8082/health
curl http://localhost:8083/health
```

### API Gateway Routes
```powershell
curl http://localhost:8080/actuator/gateway/routes
```

### Product Service - Listar productos
```powershell
curl http://localhost:8080/api/products
```

### Access Service - Tipos de cliente
```powershell
curl http://localhost:8080/api/access/client-types
```

### Session Service - Crear sesión de prueba
```powershell
curl -X POST http://localhost:8080/api/sessions `
  -H "Content-Type: application/json" `
  -d '{\"clientType\":\"FACIAL\",\"customerId\":\"TEST-001\",\"customerName\":\"Test User\"}'
```

## Paso 7: Ver logs de un servicio específico
```powershell
docker-compose logs -f api-gateway
docker-compose logs -f session-service
```

## Comandos Útiles

### Detener todo
```powershell
docker-compose down
```

### Reiniciar un servicio
```powershell
docker-compose restart session-service
```

### Ver recursos usados
```powershell
docker stats
```

### Limpiar todo (incluyendo volúmenes)
```powershell
docker-compose down -v
```
