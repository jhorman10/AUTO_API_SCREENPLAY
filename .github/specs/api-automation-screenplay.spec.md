---
id: SPEC-002
status: DRAFT
feature: api-automation-screenplay
created: 2026-03-20
updated: 2026-03-20
author: spec-generator
version: "1.0"
related-specs: ["SPEC-001"]
---

# Spec: Automatización de API REST — Patrón Screenplay con Serenity BDD

> **Estado:** `DRAFT` → aprobar con `status: APPROVED` antes de iniciar implementación.
> **Ciclo de vida:** DRAFT → APPROVED → IN_PROGRESS → IMPLEMENTED → DEPRECATED

---

## 1. REQUERIMIENTOS

### Descripción
Proyecto de automatización de pruebas (QA) que valida el ciclo completo CRUD de una API REST de Conversiones usando el patrón **Screenplay** con **Serenity BDD**. El objetivo es demostrar la construcción de tests automatizados escalables, mantenibles y alineados con estándares de la industria (BDD, Gherkin, Screenplay).

El proyecto es independiente de capas de Frontend y Backend. Es una suite de pruebas de services (API Testing) que:
1. Valida operaciones HTTP: POST, GET, PUT, DELETE
2. Sigue la arquitectura Screenplay: Actores, Tareas Abstractas, Acciones e Interacciones, Preguntas
3. Usa Serenity BDD Rest para simplificar las peticiones HTTP
4. Genera reportes ejecutivos legibles en formato Serenity HTML
5. Integra Cucumber para escribir escenarios en Gherkin

### Requerimiento de Negocio
El taller de "Maestría en Automatización: del objeto al actor" requiere que cada aprendiz entregue THREE (3) proyectos distintos para validar versatilidad técnica. Este es el **proyecto 3: API Testing (CRUD)**.

**Requisitos del Taller:**
- Patrón: Screenplay con Serenity Rest
- Escenario: Un flujo de prueba que incluya 4 verbos HTTP mínimo: POST (crear), GET (consultar), PUT (actualizar), DELETE (eliminar).
- Stack: Java, Gradle, Serenity BDD, Cucumber, GitHub Copilot
- Salida: README.md con instrucciones de ejecución + reporte Serenity HTML funcional
- Repositorio: `AUTO_API_SCREENPLAY`

**Endpoint bajo prueba:**
Se usa como referencia el requerimiento `SPEC-001: Conversiones`. Los endpoints CRUD de Conversiones son:
- `POST /api/v1/conversions` — crear conversión
- `GET /api/v1/conversions` — listar conversiones
- `GET /api/v1/conversions/{id}` — obtener conversión por ID
- `PUT /api/v1/conversions/{id}` — actualizar conversión
- `DELETE /api/v1/conversions/{id}` — eliminar conversión

### Historias de Usuario

#### HU-01: Crear conversión vía API (POST)

```
Como:        Ingeniero de QA
Quiero:      automatizar la creación de un recurso conversión
Para:        validar que el endpoint POST funciona y retorna datos consistentes

Prioridad:   Alta
Estimación:  S
Dependencias: Ninguna
Capa:        API / Pruebas
```

#### Criterios de Aceptación — HU-01

**Happy Path**
```gherkin
CRITERIO-1.1: Crear conversión con datos válidos (POST /api/v1/conversions)
  Dado que:  el Actor realiza una Tarea de crear conversión
  Y:         proporciona valores válidos (user_id, tipo, valor, fecha)
  Cuando:    envía un POST al endpoint /api/v1/conversions
  Entonces:  el servidor responde con HTTP 201 Created
  Y:         la respuesta incluye el ID generado, timestamps (created_at, updated_at), y estado "pendiente"
  Y:         los datos retornados coinciden con los enviados
```

**Error Path**
```gherkin
CRITERIO-1.2: Error al crear conversión sin datos obligatorios
  Dado que:  el request carece de un campo obligatorio (ej. user_id)
  Cuando:    se envía POST al endpoint /api/v1/conversions
  Entonces:  el servidor responde con HTTP 422 Unprocessable Entity
  Y:         la respuesta incluye detalle del error indicando el campo faltante
```

**Edge Case**
```gherkin
CRITERIO-1.3: Crear conversión con valor numérico negativo
  Dado que:  se intenta crear una conversión con valor <= 0
  Cuando:    se envía el POST
  Entonces:  el servidor responde con HTTP 422 (validación de negocio)
  Y:         el mensaje describe que el valor debe ser > 0
```

#### HU-02: Obtener conversiones vía API (GET)

```
Como:        Ingeniero de QA
Quiero:      automatizar consultas de conversiones (listado y por ID)
Para:        validar que los endpoints GET retornan datos correctos y filtrados

Prioridad:   Alta
Estimación:  S
Dependencias: HU-01
Capa:        API / Pruebas
```

#### Criterios de Aceptación — HU-02

**Happy Path**
```gherkin
CRITERIO-2.1: Obtener listado de todas las conversiones (GET /api/v1/conversions)
  Dado que:  existen conversiones previas en la base de datos
  Cuando:    se envía GET sin parámetros (o con paginación por defecto)
  Entonces:  el servidor responde con HTTP 200 OK
  Y:         la respuesta es un array de objetos conversión
  Y:         cada item incluye uid, user_id, tipo, valor, estado, created_at, updated_at
```

```gherkin
CRITERIO-2.2: Obtener conversión por ID (GET /api/v1/conversions/{id})
  Dado que:  existe una conversión con uid conocido
  Cuando:    se envía GET con el ID en la ruta
  Entonces:  el servidor responde con HTTP 200 OK
  Y:         la respuesta contiene exactamente esa conversión con todos sus campos
```

**Error Path**
```gherkin
CRITERIO-2.3: Obtener conversión con ID inexistente
  Dado que:  se solicita GET con un ID que no existe en DB
  Cuando:    se envía la petición
  Entonces:  el servidor responde con HTTP 404 Not Found
  Y:         el mensaje indica "conversión no encontrada"
```

#### HU-03: Actualizar conversión vía API (PUT)

```
Como:        Ingeniero de QA
Quiero:      automatizar la actualización parcial o total de una conversión existente
Para:        validar que el endpoint PUT modifica correctamente y mantiene integridad

Prioridad:   Alta
Estimación:  S
Dependencias: HU-01, HU-02
Capa:        API / Pruebas
```

#### Criterios de Aceptación — HU-03

**Happy Path**
```gherkin
CRITERIO-3.1: Actualizar conversión con datos válidos (PUT /api/v1/conversions/{id})
  Dado que:  existe una conversión con uid conocido
  Y:         se proporciona un nuevo valor y estado = "completada"
  Cuando:    se envía PUT con los datos modificados
  Entonces:  el servidor responde con HTTP 200 OK
  Y:         la respuesta retorna la conversión actualizada
  Y:         updated_at se actualiza a la fecha/hora actual (distinta de created_at)
  Y:         los datos no modificados se mantienen
```

**Error Path**
```gherkin
CRITERIO-3.2: Error al actualizar conversión inexistente
  Dado que:  se intenta actualizar con un ID que no existe
  Cuando:    se envía PUT a /api/v1/conversions/{invalid_id}
  Entonces:  el servidor responde con HTTP 404 Not Found
```

```gherkin
CRITERIO-3.3: Error de validación al actualizar con valor inválido
  Dado que:  se intenta actualizar el campo valor a un número negativo
  Cuando:    se envía PUT
  Entonces:  el servidor responde con HTTP 422 (validación de negocio)
```

#### HU-04: Eliminar conversión vía API (DELETE)

```
Como:        Ingeniero de QA
Quiero:      automatizar la eliminación de una conversión
Para:        validar que el endpoint DELETE funciona y elimina correctamente

Prioridad:   Alta
Estimación:  S
Dependencias: HU-01, HU-02, HU-03
Capa:        API / Pruebas
```

#### Criterios de Aceptación — HU-04

**Happy Path**
```gherkin
CRITERIO-4.1: Eliminar conversión exitosamente (DELETE /api/v1/conversions/{id})
  Dado que:  existe una conversión con uid conocido
  Cuando:    se envía DELETE a /api/v1/conversions/{id}
  Entonces:  el servidor responde con HTTP 204 No Content
  Y:         la conversión desaparece de la base de datos
```

**Error Path**
```gherkin
CRITERIO-4.2: Error al eliminar conversión inexistente
  Dado que:  se intenta eliminar un ID que no existe
  Cuando:    se envía DELETE
  Entonces:  el servidor responde con HTTP 404 Not Found
```

### Reglas de Negocio
1. **Campos obligatorios (POST):** `user_id`, `tipo`, `valor`. Fechas se auto-generan.
2. **Validación de valor:** Debe ser numérico, positivo (> 0).
3. **Estados válidos:** `pendiente`, `completada`, `cancelada`.
4. **Unicidad:** No hay restricción de unicidad en el nivel de API (múltiples conversiones del mismo usuario permitidas).
5. **Timestamps:** Siempre en UTC, formato ISO 8601, snake_case (`created_at`, `updated_at`).
6. **Respuesta DELETE:** HTTP 204 sin body.

---

## 2. DISEÑO

### Modelos de Datos (para Entender la API)

#### Entidad: Conversión
| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `uid` | UUID string | sí | auto-generado (POST response) | Identificador único de la conversión |
| `user_id` | string | sí | no vacío | UID del usuario que realizó la conversión |
| `tipo` | string | sí | enum: `venta`, `descarga`, `suscripcion`, `otros` | Categoría de la conversión |
| `valor` | float | sí | > 0 | Monto o ingresos asociados |
| `estado` | string | sí (def: "pendiente") | enum: `pendiente`, `completada`, `cancelada` | Estado de la conversión |
| `created_at` | datetime (UTC) | sí | auto-generado | Timestamp de creación (ISO 8601) |
| `updated_at` | datetime (UTC) | sí | auto-generado | Timestamp de última modificación (ISO 8601) |

### API Endpoints a Probar

#### POST /api/v1/conversions
- **Descripción**: Crea una nueva conversión
- **Method**: POST
- **Auth requerida**: Bearer Token (implicit en "API propia")
- **Request Body**:
  ```json
  {
    "user_id": "string",
    "tipo": "venta|descarga|suscripcion|otros",
    "valor": 100.50,
    "estado": "pendiente" (opcional, por defecto)
  }
  ```
- **Response 201 Created**:
  ```json
  {
    "uid": "uuid-string",
    "user_id": "string",
    "tipo": "venta",
    "valor": 100.50,
    "estado": "pendiente",
    "created_at": "2026-03-20T10:30:00Z",
    "updated_at": "2026-03-20T10:30:00Z"
  }
  ```
- **Response 422 Unprocessable Entity**: Validación fallida (campo faltante, valor inválido, etc.)

#### GET /api/v1/conversions
- **Descripción**: Obtiene todas las conversiones (con paginación opcional)
- **Method**: GET
- **Auth requerida**: Bearer Token
- **Query Params** (opcionales): `page`, `limit`
- **Response 200 OK**:
  ```json
  [
    {
      "uid": "uuid-1",
      "user_id": "user-1",
      "tipo": "venta",
      "valor": 100.50,
      "estado": "pendiente",
      "created_at": "2026-03-20T10:30:00Z",
      "updated_at": "2026-03-20T10:30:00Z"
    }
  ]
  ```

#### GET /api/v1/conversions/{id}
- **Descripción**: Obtiene una conversión específica por su uid
- **Method**: GET
- **Auth requerida**: Bearer Token
- **Path Param**: `id` (uid de la conversión)
- **Response 200 OK**: Objeto conversión completo
- **Response 404 Not Found**: Conversión no existe

#### PUT /api/v1/conversions/{id}
- **Descripción**: Actualiza una conversión existente
- **Method**: PUT
- **Auth requerida**: Bearer Token
- **Path Param**: `id`
- **Request Body** (campos opcionales):
  ```json
  {
    "tipo": "venta",
    "valor": 150.75,
    "estado": "completada"
  }
  ```
- **Response 200 OK**: Conversión actualizada con `updated_at` nuevo
- **Response 404 Not Found**: Conversión no existe
- **Response 422 Unprocessable Entity**: Validación fallida

#### DELETE /api/v1/conversions/{id}
- **Descripción**: Elimina una conversión
- **Method**: DELETE
- **Auth requerida**: Bearer Token
- **Path Param**: `id`
- **Response 204 No Content**: Eliminado exitosamente (sin body)
- **Response 404 Not Found**: Conversión no existe

### Diseño de Arquitectura Screenplay

#### Actor
- **Actor:** `ConversionUser` o `ApiTester` (el usuario que interactúa con la API)

#### Tareas (Tasks)
1. **`CreateConversion`** — encapsula la lógica POST /api/v1/conversions
   - Responsabilidad única: preparar y enviar una petición POST con datos válidos
   - Recibe parámetros: user_id, tipo, valor
   - Retorna: uid de la conversión creada
   
2. **`FetchAllConversions`** — encapsula GET /api/v1/conversions
   - Responsabilidad única: obtener listado de conversiones
   - Recibe: parámetros opcionales (page, limit)
   - Retorna: lista de conversiones

3. **`FetchConversionById`** — encapsula GET /api/v1/conversions/{id}
   - Responsabilidad única: obtener una conversión por su ID
   - Recibe: id (uid)
   - Retorna: objeto conversión

4. **`UpdateConversion`** — encapsula PUT /api/v1/conversions/{id}
   - Responsabilidad única: actualizar un recurso existente
   - Recibe: id, datos a actualizar (tipo, valor, estado)
   - Retorna: conversión actualizada

5. **`DeleteConversion`** — encapsula DELETE /api/v1/conversions/{id}
   - Responsabilidad única: eliminar una conversión
   - Recibe: id
   - Retorna: status 204

#### Acciones e Interacciones
- Usar **Serenity Rest** (`SerenityRest`) para realizar las peticiones HTTP
- Log automático de Request/Response en reportes

#### Preguntas (Questions)
1. **`ConversionId`** — pregunta sobre el ID de la última conversión creada
2. **`ConversionList`** — pregunta que retorna la lista de conversiones
3. **`ConversionStatus`** — pregunta que valida el estado de una conversión (pendiente, completada, etc.)
4. **`ResponseStatus`** — pregunta que retorna el código HTTP de la última respuesta

#### Paso a Paso: Flujo CRUD en Screenplay

```
Escenario: Ciclo Completo de Conversiones (C + R + U + D)
  Actor: ApiTester

  1. CREATE:  Actor ejecuta CreateConversion con datos válidos
              → Retorna uid de la conversión
              → Question: ConversionId responde con el uid

  2. READ (listado): Actor ejecuta FetchAllConversions
                     → Valida que la conversión nueva esté en la lista
                     → Question: ConversionList contiene el nuevo item

  3. READ (por ID): Actor ejecuta FetchConversionById(uid)
                    → Valida que retorna la conversión con datos correctos
                    → Question: ConversionStatus == "pendiente"

  4. UPDATE: Actor ejecuta UpdateConversion(uid, {estado: "completada"})
             → Valida que updated_at es más reciente que created_at
             → Valida que estado cambió a "completada"
             → Question: ConversionStatus == "completada"

  5. DELETE: Actor ejecuta DeleteConversion(uid)
             → Valida que responde 204
             → Valida que al intentar GET no se encuentra la conversión

  Resultado: Ciclo CRUD completado exitosamente
             Reporte Serenity HTML generado con detalles de cada paso
```

### Configuración de Proyecto

#### Estructura de Carpetas
```
AUTO_API_SCREENPLAY/
├── build.gradle                            # Dependencias y configuración Gradle
├── gradle/
│   └── wrapper/                            # Gradle Wrapper
├── src/
│   ├── test/
│   │   ├── java/
│   │   │   └── com/maestria/qa/
│   │   │       ├── actors/
│   │   │       │   └── ApiTester.java
│   │   │       ├── tasks/
│   │   │       │   ├── CreateConversion.java
│   │   │       │   ├── FetchAllConversions.java
│   │   │       │   ├── FetchConversionById.java
│   │   │       │   ├── UpdateConversion.java
│   │   │       │   └── DeleteConversion.java
│   │   │       ├── questions/
│   │   │       │   ├── ConversionId.java
│   │   │       │   ├── ConversionList.java
│   │   │       │   ├── ConversionStatus.java
│   │   │       │   └── ResponseStatus.java
│   │   │       ├── stepdefs/
│   │   │       │   └── ConversionStepDefinitions.java
│   │   │       ├── utils/
│   │   │       │   ├── ApiConfig.java
│   │   │       │   └── ConversionDataFactory.java
│   │   │       └── runners/
│   │   │           └── CucumberRunner.java
│   │   └── resources/
│   │       ├── serenity.conf               # Configuración Serenity
│   │       ├── features/
│   │       │   └── conversions.feature     # Escenarios Gherkin
│   │       └── data/
│   │           └── test-data.json          # Datos mock
├── serenity-junit-runner-jar-with-dependencies.jar
├── serenity.conf                           # Configuración global Serenity
├── README.md                               # Instrucciones de ejecución
└── .gitignore

```

#### Dependencies Clave (Gradle)
```gradle
// Serenity BDD
serenity-core
serenity-rest-assured
serenity-cucumber6

// Cucumber
cucumber-java
cucumber-junit
cucumber-picocontainer

// REST Assured (usado por Serenity)
rest-assured
json-path
xmlpath

// JSON & Logging
gson
jackson-databind
slf4j-api
logback-classic

// Testing
junit
hamcrest
```

#### serenity.conf
```properties
webdriver.driver=rest
serenity.test.root=com.maestria.qa
serenity.report.encoding=UTF-8
serenity.logging=VERBOSE

# API Base URL
api.base.url=http://localhost:8000/api/v1
api.timeout=5000
api.max.retries=2

# Reporte
serenity.outputDirectory=target/site/serenity
```

### Notas de Implementación
1. **No usar Page Objects:** Este es un proyecto de API Testing, no Frontend. No hay UI para modelar.
2. **Base URL configurable:** Usar `serenity.conf` para ambiente (local, staging, producción).
3. **Data Factory:** Crear un `ConversionDataFactory` que genere datos válidos para pruebas (user_id aleatorio, valores realistas, etc.).
4. **Independencia de tests:** Cada escenario debe ser independiente. No confiar en el estado de corridas anteriores (usar el mismo uid para limpiar en teardown o crear datos únicos).
5. **Reportes Serenity:** Automáticamente genera HTML con screenshots (en este caso, request/response logs).
6. **Error Assertions:** Usar Hamcrest matchers o AssertJ para validar responses (status code, json path, etc.).

---

## 3. LISTA DE TAREAS

> Checklist para el agente de implementación (Backend Developer o Test Engineer).

### Configuración Inicial
- [ ] Crear estructura de carpetas base del proyecto
- [ ] Inicializar `build.gradle` con dependencias de Serenity, Cucumber, REST Assured
- [ ] Crear `serenity.conf` con configuración de base URL y timeout
- [ ] Configurar `cucumber.options` en runner properties
- [ ] Crear `CucumberRunner.java` con anotaciones `@RunWith(CucumberWithSerenity.class)`

### Implementación de Tareas (Tasks)
- [ ] Implementar `CreateConversion.java` (Task que realiza POST /api/v1/conversions)
  - [ ] Usar `SerenityRest.post()`
  - [ ] Capturar el `uid` de la respuesta en una variable compartida o Question
  
- [ ] Implementar `FetchAllConversions.java` (Task de GET /api/v1/conversions)
  
- [ ] Implementar `FetchConversionById.java` (Task de GET /api/v1/conversions/{id})
  
- [ ] Implementar `UpdateConversion.java` (Task de PUT /api/v1/conversions/{id})
  - [ ] Validar que `updated_at` es más reciente que `created_at`
  
- [ ] Implementar `DeleteConversion.java` (Task de DELETE /api/v1/conversions/{id})

### Implementación de Preguntas (Questions)
- [ ] Implementar `ConversionId.java` (retorna el último uid creado)
- [ ] Implementar `ConversionList.java` (retorna el listado completo)
- [ ] Implementar `ConversionStatus.java` (retorna el estado actual de una conversión)
- [ ] Implementar `ResponseStatus.java` (retorna HTTP status code)

### Implementación de Step Definitions
- [ ] Implementar `ConversionStepDefinitions.java`
  - [ ] Step: `Given el ApiTester está listo para crear conversiones`
  - [ ] Step: `When crea una conversión con tipo {string} y valor {float}`
  - [ ] Step: `Then la conversión se crea exitosamente con status 201`
  - [ ] Step: `When obtiene la lista de conversiones`
  - [ ] Step: `Then la lista contiene al menos 1 conversión`
  - [ ] Step: `When obtiene la conversión por su id`
  - [ ] Step: `Then los datos retornados coinciden con los creados`
  - [ ] Step: `When actualiza el estado a {string}`
  - [ ] Step: `Then el estado se actualiza correctamente`
  - [ ] Step: `When elimina la conversión`
  - [ ] Step: `Then la conversión se elimina exitosamente con status 204`

### Escenarios Gherkin
- [ ] Crear `features/conversions.feature`
  - [ ] Escenario 1: Happy Path — CRUD Completo (POST → GET → GET by ID → PUT → DELETE)
  - [ ] Escenario 2: Error Path — POST sin datos obligatorios
  - [ ] Escenario 3: Error Path — GET con ID inexistente
  - [ ] Escenario 4: Error Path — PUT con ID inexistente
  - [ ] Escenario 5: Error Path — DELETE con ID inexistente

### Utilidades y Setup
- [ ] Crear `ApiConfig.java` (carga base URL de `serenity.conf`)
- [ ] Crear `ConversionDataFactory.java` (generador de datos mock)
- [ ] Crear `hooks/` o `@Before/@After` en steps para setup/teardown

### Testing
- [ ] Ejecutar `gradle test` o `mvn verify`
- [ ] Validar que todos los escenarios pasan
- [ ] Validar que los reportes Serenity se generan en `target/site/serenity/index.html`

### Documentación
- [ ] Crear/completar `README.md` con:
  - [ ] Descripción del proyecto
  - [ ] Requisitos previos (Java version, Gradle version)
  - [ ] Cómo ejecutar los tests: `gradle test`
  - [ ] Dónde encontrar los reportes: `target/site/serenity/index.html`
  - [ ] Estructura del proyecto (carpetas clave)
  - [ ] Cómo extender (ejemplo: agregar nuevo escenario)
  - [ ] Stack tecnológico usado

### Quality Gates
- [ ] Código: Sin comentarios innecesarios, nomenclatura semántica, SOLID
- [ ] Gherkin: Escenarios declarativos, sin coupling, enfocados en comportamiento
- [ ] Reportes: Serenity HTML completo con logs de cada paso
- [ ] Independencia: Ningún test depende de otro; pueden ejecutarse en cualquier orden

---

## 4. CONSIDERACIONES ADICIONALES

### Scope Excluido (Fase 2 o Posterior)
1. Performance testing (load testing, stress testing) — requiere framework como k6
2. Autenticación/Autorización con tokens reales — usar mock o credenciales de test
3. CI/CD Pipeline — puede agregarse luego (GitHub Actions, Jenkins)
4. Integración con herramientas de reporting (Slack, email) — manual por ahora

### Riesgos Identificados
1. **Riesgo: API no disponible en tiempo de test**
   - Mitigación: Usar stub/mock si es necesario; documentar en README

2. **Riesgo: Datos inconsistentes entre corridas**
   - Mitigación: Usar ConversionDataFactory para generar datos únicos; limpiar al finalizar (DELETE)

3. **Riesgo: Flakiness en tests**
   - Mitigación: Agregar retries en Serenity; usar timeouts adecuados; evitar sleep()

### Referencias
- Serenity BDD: https://serenity-bdd.info/
- Screenplay Pattern: https://serenity-bdd.info/docs/screenplay/
- REST Assured: https://rest-assured.io/
- Cucumber: https://cucumber.io/

---

**Siguiente paso:** Mover spec a `status: APPROVED` y delegar implementación a agente Backend Developer o Test Engineer con skill `/implement-backend` o equivalente de automatización.
