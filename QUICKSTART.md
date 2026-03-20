# 🚀 QUICKSTART - AUTO_API_SCREENPLAY

Guía rápida para ejecutar los tests de automatización de API REST con Serenity BDD Screenplay.

---

## 📦 Instalación Inicial (Una sola vez)

```bash
cd AUTO_API_SCREENPLAY

# Compilar el proyecto
gradle clean build

# Verificar que todo esté listo
gradle test --dry-run
```

---

## ▶️ Ejecutar Tests

### Ejecutar TODO
```bash
gradle test
```

### Ejecutar SOLO escenarios críticos
```bash
gradle test -Dcucumber.filter.tags="@critical"
```

### Ejecutar SOLO escenarios "smoke" (rápidos)
```bash
gradle test -Dcucumber.filter.tags="@smoke"
```

### Ejecutar escenarios NEGATIVOS (validación de errores)
```bash
gradle test -Dcucumber.filter.tags="@negative"
```

### Ejecutar por NOMBRE de escenario
```bash
gradle test -Dcucumber.filter.name="Ciclo Completo"
```

---

## 📊 Ver Reportes

Después de ejecutar `gradle test`, los reportes se generan automáticamente:

### Abrir el reporte HTML (recomendado)
```bash
# macOS
open target/site/serenity/index.html

# Linux
xdg-open target/site/serenity/index.html

# Windows
start target/site/serenity/index.html
```

### Ubicaciones de reportes:
- **HTML Report**: `target/site/serenity/index.html`
- **JSON (Cucumber)**: `target/cucumber-reports/cucumber.json`
- **XML (JUnit)**: `target/cucumber-reports/cucumber.xml`

---

## ⚙️ Cambiar la URL de la API

En `serenity.conf`, modifica la línea:
```properties
api.base.url=http://localhost:8000/api/v1
```

O ejecuta con directamente:
```bash
gradle test -Dapi.base.url=http://otra-api.com
```

---

## 🐛 Solucionar Problemas Comunes

### ❌ "Connection refused"
```
→ La API no está corriendo en http://localhost:8000/api/v1
→ Verifica que la API esté iniciada
→ Revisa la URL en serenity.conf
```

### ❌ "No tests were executed"
```
→ Verifica que los archivos .feature estén en src/test/resources/features/
→ Verifica que @RunWith(Cucumber.class) esté en CucumberRunner.java
```

### ❌ "JSONPath error"
```
→ La estructura de respuesta no coincide con lo esperado
→ Abre el navegador con el reporte y ve la respuesta real
→ Actualiza las Questions según la estructura real
```

### ❌ Long running tests
```
→ Aumenta el timeout en serenity.conf:
   api.timeout=10000
```

---

## 📋 Estructura de un Test

Cada test sigue esta secuencia:

```
Feature (Gherkin)
  ↓
StepDefinitions (Steps en español)
  ↓
Tasks (Acciones que realiza el actor)
  ↓
REST Calls (POST, GET, PUT, DELETE)
  ↓
Questions (Validaciones de la respuesta)
  ↓
Assertion (should seeThat...)
```

---

## 🎯 Ejemplo de Ejecución

```bash
# 1. Ejecutar un test
gradle test -Dcucumber.filter.name="Ciclo Completo"

# 2. Esperar a que termine (toma ~5 segundos)
# ✓ POST create conversion (201)
# ✓ GET all conversions (200)
# ✓ GET by ID (200)
# ✓ PUT update status (200)
# ✓ DELETE conversion (204)

# 3. Abrir reporte
open target/site/serenity/index.html

# 4. Ver cada paso ejecutado en el reporte con:
#    - Datos enviados (Request)
#    - Datos recibidos (Response)
#    - Validaciones (Assertions)
#    - Logs de cada tarea
```

---

## 💡 Tips de Productividad

### Ejecutar solo un paquete
```bash
gradle test --tests "ConversionStepDefinitions"
```

### Ver logs en consola
```bash
gradle test --info
gradle test --debug
```

### Ejecutar sin limpiar antes (más rápido)
```bash
gradle test --no-clean
```

### Ejecutar en paralelo (4 threads)
```bash
gradle test --parallel --max-workers=4
```

---

## 📚 Más Info

- [Documentación Completa](./README_SCREENPLAY.md)
- [Patrón Screenplay](https://serenity-bdd.info/docs/screenplay/screenplay_pattern)
- [Cucumber/Gherkin](https://cucumber.io/docs/gherkin/)

---

**¿Problemas?** Revisa los reportes en `target/site/serenity/` — cada paso queda documentado.
