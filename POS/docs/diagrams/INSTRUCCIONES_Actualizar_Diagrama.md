# Instrucciones para Actualizar Diagrama de Arquitectura en Draw.io

**Archivo a editar**: `Arquitectura_Microservicios.drawio` (o trabajar en la copia v2)

## Pasos para Agregar Capa 0: API Externa

### 1. Abrir el archivo en draw.io
- Abre `docs/diagrams/Arquitectura_Microservicios.drawio`
- O crea desde la copia: `Arquitectura_Microservicios_v2_API-Externa_2025-12-22_22-37.drawio`

### 2. Seleccionar todas las capas existentes
- Clic en el fondo para deseleccionar todo
- Presiona `Ctrl + A` para seleccionar todo
- En el panel derecho, ve a "Arrange" → "Position"
- Aumenta el valor Y en +100px para todas las capas (esto las mueve hacia abajo)

### 3. Crear la nueva Capa 0
**Ubicación**: Y=80, ancho completo del diagrama

**Caja contenedora de la capa**:
- **Tipo**: Rectangle con esquinas redondeadas
- **Posición**: X=40, Y=80
- **Tamaño**: Width=1574, Height=100
- **Color de relleno**: Negro (#000000)
- **Color de borde**: Violeta (#9673a6)
- **Borde**: Grosor 2px
- **Texto**: "CAPA 0: API EXTERNA (Sistemas Terceros → Alwon)"
- **Estilo texto**: Negrita, 14pt, alineado arriba-centro

### 4. Agregar el servicio External API
**Dentro de la Capa 0**:

**Caja del servicio**:
- **Tipo**: Rectangle con esquinas redondeadas
- **Posición**: X=627, Y=110
- **Tamaño**: Width=400, Height=60
- **Color de relleno**: Negro (#000000)
- **Color de borde**: Violeta (#9673a6)
- **Color de texto**: Blanco (#FFFFFF)
- **Borde**: Grosor 2px
- **Texto** (multi-línea):
  ```
  External Customer API
  Spring Boot
  :9000
  
  POST /api/external/customer
  POST /api/external/purchase
  ```
- **Estilo texto**: Negrita, 11pt, centrado

### 5. Agregar conexiones (flechas)
**De External API → API Gateway**:
- **Tipo**: Flecha con línea recta u orthogonal
- **Color**: Violeta (#9673a6) 
- **Grosor**: 2px
- **Estilo**: Línea sólida
- **Etiqueta**: "REST API (External)"

**De Sistemas Externos → External API** (opcional):
- Agregar una caja simple arriba etiquetada "Sistemas Terceros (IA/Cámaras)"
- Flecha punteada hacia External API
- Etiqueta: "HTTP POST"

### 6. Actualizar el título
Cambiar el título a:
```
ARQUITECTURA MICROSERVICIOS - ALWON POS (v2 - API Externa)
```

### 7. Ajustar leyenda (opcional)
En la caja de leyenda (abajo a la derecha), agregar:
```
— Violeta (sólida): API Externa
```

### 8. Verificar posiciones finales
**Después de los ajustes, las capas deben estar en**:
- Capa 0 (Externa): Y=80-180
- Capa 1 (Frontend): Y=200-320
- Capa 2 (Gateway/WebSocket): Y=360-480
- Capa 3 (Microservicios): Y=520-860
- Capa 4 (Bases de Datos): Y=900+

### 9. Guardar
- `Ctrl + S` o File → Save
- Si trabajaste en la copia, guárdala con el nombre versionado

## Valores Clave de Referencia

| Elemento | X | Y | Width | Height | Color Borde |
|----------|---|---|-------|--------|-------------|
| Capa 0 Container | 40 | 80 | 1574 | 100 | #9673a6 |
| External API Service | 627 | 110 | 400 | 60 | #9673a6 |
| Frontend Layer | 40 | 200 | 1574 | 120 | #82b366 |
| Gateway Layer | 40 | 360 | 740 | 120 | #6c8ebf |

## Alternativa: Diagrama Mermaid

Si prefieres, puedo generar un diagrama Mermaid en texto que muestre la arquitectura completa con la nueva capa.

¿Prefieres que te ayude de otra forma o con este manual es suficiente?
