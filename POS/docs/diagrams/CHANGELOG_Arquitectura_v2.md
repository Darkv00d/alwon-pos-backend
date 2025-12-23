# Cambios en Diagrama de Arquitectura

**Archivo**: `Arquitectura_Microservicios_v2_API-Externa_2025-12-22_22-37.drawio`  
**Fecha**: 2025-12-22 22:37  
**Basado en**: `Arquitectura_Microservicios.drawio` (original intacto)

## Cambios Realizados

### 1. Nueva Capa 0: API Externa
- **Ubicación**: Arriba de la capa de Frontend
- **Color**: Violeta (#9673a6) para diferenciación visual
- **Componente**: External Customer API Service (puerto 9000)

### 2. Endpoints Expuestos
El servicio de API Externa expone dos endpoints:
- `POST /api/external/customer` - Registro de cliente
- `POST /api/external/purchase` - Registro de compra

### 3. Flujo de Datos
```
Sistema Terceros (IA/Cámaras)
    ↓
External Customer API (:9000)
    ↓
API Gateway (:8080)
    ↓
Microservicios (Session, Cart, Product, Access, Camera)
```

### 4. Ajustes de Posicionamiento
- Todas las capas existentes se movieron 100px hacia abajo (Y +100)
- Capa 0 (Externa): Y=80-180
- Capa 1 (Frontend): Y=200-320
- Capa 2 (Gateway/WebSocket): Y=360-480
- Capa 3 (Microservicios): Y=520-860

### 5. Características del Nuevo Servicio
- **Puerto**: 9000
- **Tecnología**: Spring Boot
- **Función**: Recibir datos de sistemas externos (AI, cámaras, sensores)
- **Archivos**: Fotos, GIF, videos (temporales)
- **Tipos de ingreso**: FACIAL, PIN, NO_IDENTIFICADO

## Notas Técnicas

- El archivo original permanece intacto
- Esta versión incluye la nueva capa en el diseño
- Se mantiene la coherencia de colores y estilos
- Título actualizado con versión y fecha

## Ver Diagrama

Para visualizar los cambios, abrir el archivo con draw.io:
- **Archivo**: `C:\Users\algam\.gemini\antigravity\scratch\Alwon\POS\docs\diagrams\Arquitectura_Microservicios_v2_API-Externa_2025-12-22_22-37.drawio`
