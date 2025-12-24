# Alwon POS - Solución de Problemas de Compilación

## ⚠️ Problema Detectado

**Error:** `Fatal error compiling: invalid flag: --release`

## 🔍 Diagnóstico

Maven está usando **Java 8** en lugar de **Java 21**. 

Verificación actual:
- `JAVA_HOME`: `C:\Users\algam\Java\java-1.8.0-openjdk-1.8.0.392-1.b08.redhat.windows.x86_64`
- `java -version`: OpenJDK 21.0.9 ✅
- `javac`: No encontrado en PATH (usando Java 8)

## ✅ Solución

### Opción 1: Configurar JAVA_HOME (Recomendado)

**1. Encuentra Java 21:**
```powershell
# Busca en ubicaciones comunes
dir "C:\Program Files\Eclipse Adoptium\" -Recurse -Filter "java.exe" | Select-Object FullName
dir "C:\Program Files\Java\" -Recurse  -Filter "java.exe" | Select-Object FullName
```

**2. Configurar JAVA_HOME (Sesión Actual):**
```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.9.10-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
```

**3. Configurar JAVA_HOME (Permanente):**
- Presiona `Win + X` → "Sistema"
- Click en "Configuración avanzada del sistema"
- Click en "Variables de entorno"
- En "Variables del sistema":
  - Crea/Edita `JAVA_HOME` = `C:\Program Files\Eclipse Adoptium\jdk-21.0.9.10-hotspot`
  - Edita `Path` y agrega al INICIO: `%JAVA_HOME%\bin`

**4. Verificar:**
```powershell
# Reinicia PowerShell
java -version    # Debe mostrar: OpenJDK 21.0.9
javac -version   # Debe mostrar: javac 21.0.9
mvn -version     # Debe mostrar Java 21
```

### Opción 2: Configurar Maven Toolchains

Crea `~/.m2/toolchains.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<toolchains>
  <toolchain>
    <type>jdk</type>
    <provides>
      <version>21</version>
    </provides>
    <configuration>
      <jdkHome>C:\Program Files\Eclipse Adoptium\jdk-21.0.9.10-hotspot</jdkHome>
    </configuration>
  </toolchain>
</toolchains>
```

### Opción 3: Forzar Java en Maven

Temporal para un build:
```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.9.10-hotspot"
mvn clean package -DskipTests
```

## 🔄 Reintentar Compilación

Después de configurar `JAVA_HOME`:

```powershell
cd backend
.\build-all.ps1
```

## 📍 Buscar Java 21

Ejecuta este comando para encontrar Java 21:
```powershell
Get-ChildItem -Path "C:\Program Files" -Recurse -Filter "java.exe" -ErrorAction SilentlyContinue | 
  Where-Object { $_.FullName -match "21" } | 
  Select-Object FullName
```
