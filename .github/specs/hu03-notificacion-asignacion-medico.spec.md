---
id: SPEC-004
status: IMPLEMENTED
feature: hu03-notificacion-asignacion-medico
created: 2026-04-08
updated: 2026-04-08
author: spec-generator
version: "1.0"
related-specs: ["SPEC-002", "SPEC-003"]
---

# Spec: Automatización HU-03 — Notificación de Asignación de Médico

> **Estado:** `DRAFT` → aprobar con `status: APPROVED` antes de iniciar implementación.  
> **Ciclo de vida:** DRAFT → APPROVED → IN_PROGRESS → IMPLEMENTED

---

## 1. REQUERIMIENTOS

### Descripción
Automatización de pruebas de API REST para la **HU-03: Notificación de Asignación de Médico** del Sistema Inteligente de Gestión de Turnos Médicos. Se extiende la suite existente `AUTO_API_SCREENPLAY` con nuevos escenarios Gherkin, Tasks, Questions y Step Definitions bajo el patrón **Screenplay con Serenity BDD**.

La suite valida el flujo completo: registro de turno con urgencia → consulta de posición en cola → asignación de médico disponible → verificación de notificación y auditoría. Incluye flujos positivos, negativos y de borde.

### Requerimiento de Negocio
La HU-03 establece que:
- Cuando un turno se asigna, el paciente recibe notificación en pantalla en ≤2 segundos.
- La notificación muestra: nombre del médico, consultorio asignado y hora estimada.
- Si no hay asignación, no se muestra notificación de llamado.
- Cada notificación deja registro auditable con timestamp y referencias del turno.

Soportada por habilitadores técnicos:
- **HT-04**: Motor de asignación por urgencia y disponibilidad
- **HT-05**: Publicación confiable de evento `AppointmentAssigned`
- **HT-06**: Notificación visible y auditable al paciente

### Historias de Usuario de Automatización

#### HU-AUTO-01: Validar registro de turno con urgencia (POST)

```
Como:        Ingeniero de QA
Quiero:      Automatizar la creación de turnos con niveles de urgencia (Alta, Media, Baja)
Para:        Validar que el endpoint persiste correctamente la urgencia y el estado inicial

Prioridad:   Alta
Estimación:  S
Dependencias: Ninguna
Capa:        API / Pruebas
```

#### Criterios de Aceptación — HU-AUTO-01

**Happy Path**
```gherkin
CRITERIO-1.1: Crear turno con urgencia válida
  Dado que:  El actor se autentica como recepcionista
  Cuando:    Crea un turno con cédula 123456, nombre "Juan García" y urgencia "Alta"
  Entonces:  El servidor responde HTTP 202
  Y:         La respuesta contiene state "waiting"
  Y:         La respuesta contiene urgency "Alta"
  Y:         La respuesta contiene created_at con formato ISO 8601
```

**Error Path**
```gherkin
CRITERIO-1.2: Rechazo de turno sin urgencia
  Dado que:  El actor intenta crear un turno sin campo urgency
  Cuando:    Envía POST al endpoint de turnos
  Entonces:  El servidor responde HTTP 400
  Y:         El mensaje indica "Urgencia es obligatoria"
```

```gherkin
CRITERIO-1.3: Rechazo de urgencia inválida
  Dado que:  El actor intenta crear un turno con urgency "Crítica"
  Cuando:    Envía POST al endpoint
  Entonces:  El servidor responde HTTP 400
  Y:         El mensaje indica valores permitidos: Alta, Media, Baja
```

#### HU-AUTO-02: Validar consulta de posición en cola (GET)

```
Como:        Ingeniero de QA
Quiero:      Automatizar la consulta de posición en cola ordenada por urgencia + FIFO
Para:        Verificar que el ordenamiento respeta prioridad y created_at

Prioridad:   Alta
Estimación:  M
Dependencias: HU-AUTO-01
Capa:        API / Pruebas
```

#### Criterios de Aceptación — HU-AUTO-02

**Happy Path**
```gherkin
CRITERIO-2.1: Consultar posición de turno en cola
  Dado que:  Existe un turno registrado con id conocido en estado "waiting"
  Cuando:    Se consulta GET /turnos/queue/position/{id}
  Entonces:  El servidor responde HTTP 200
  Y:         La respuesta contiene position (número entero ≥ 1)
  Y:         La respuesta contiene total_in_queue
  Y:         La respuesta contiene state "waiting"
```

```gherkin
CRITERIO-2.2: Orden de cola respeta urgencia + FIFO
  Dado que:  El actor registra 3 turnos en orden:
             | nombre    | urgencia | orden |
             | Paciente1 | Baja     | 1     |
             | Paciente2 | Alta     | 2     |
             | Paciente3 | Media    | 3     |
  Cuando:    Consulta la cola completa GET /turnos/queue
  Entonces:  El primer turno es "Paciente2" (Alta)
  Y:         El segundo turno es "Paciente3" (Media)
  Y:         El tercer turno es "Paciente1" (Baja)
```

**Error Path**
```gherkin
CRITERIO-2.3: Turno inexistente
  Dado que:  Se consulta posición con un ID inexistente
  Cuando:    Se envía GET /turnos/queue/position/{invalid_id}
  Entonces:  El servidor responde HTTP 404
  Y:         El mensaje indica "Turno no encontrado"
```

**Edge Case**
```gherkin
CRITERIO-2.4: Turno ya asignado no tiene posición
  Dado que:  Un turno fue asignado a un médico (state="assigned")
  Cuando:    Se consulta su posición
  Entonces:  El servidor responde HTTP 200
  Y:         El campo position es null
  Y:         El campo state es "assigned"
  Y:         Contiene assigned_doctor_name y assigned_office_number
```

#### HU-AUTO-03: Validar motor de asignación y notificación (E2E)

```
Como:        Ingeniero de QA
Quiero:      Automatizar la validación del motor de asignación inteligente
Para:        Verificar que asigna por urgencia, valida disponibilidad de médico y genera auditoría

Prioridad:   Alta
Estimación:  L
Dependencias: HU-AUTO-01, HU-AUTO-02
Capa:        API / Pruebas
```

#### Criterios de Aceptación — HU-AUTO-03

**Happy Path**
```gherkin
CRITERIO-3.1: Asignación exitosa a médico disponible
  Dado que:  Existe al menos un médico en estado "available"
  Y:         Se registra un turno con urgencia "Alta"
  Cuando:    El motor de asignación procesa la cola (polling en ≤10 segundos)
  Entonces:  El turno transiciona a state "assigned"
  Y:         La respuesta incluye assigned_doctor_name (no null)
  Y:         La respuesta incluye assigned_office_number (≥ 1)
  Y:         La respuesta incluye assigned_at con formato ISO 8601
```

```gherkin
CRITERIO-3.2: Prioridad Alta se asigna antes que Media
  Dado que:  Existe un solo médico disponible
  Y:         Se registran 2 turnos: primero "Media" y luego "Alta"
  Cuando:    El motor de asignación procesa
  Entonces:  El turno "Alta" se asigna primero
  Y:         El turno "Media" permanece en "waiting"
```

**Error Path**
```gherkin
CRITERIO-3.3: Sin médico disponible, turno permanece en espera
  Dado que:  No hay médicos en estado "available"
  Y:         Se registra un turno con urgencia "Alta"
  Cuando:    Se consulta el turno tras 5 segundos
  Entonces:  El turno permanece en state "waiting"
  Y:         assigned_doctor_name es null
  Y:         assigned_office_number es null
```

**Edge Case**
```gherkin
CRITERIO-3.4: FIFO dentro de misma urgencia
  Dado que:  Se registran 2 turnos con urgencia "Media" con 1 segundo de diferencia
  Y:         Solo 1 médico disponible
  Cuando:    El motor de asignación procesa
  Entonces:  El turno con created_at más antiguo se asigna primero
```

#### HU-AUTO-04: Validar auditoría de asignación

```
Como:        Ingeniero de QA
Quiero:      Verificar que cada asignación genera un registro de auditoría
Para:        Garantizar trazabilidad completa de las decisiones del sistema

Prioridad:   Media
Estimación:  S
Dependencias: HU-AUTO-03
Capa:        API / Pruebas
```

#### Criterios de Aceptación — HU-AUTO-04

**Happy Path**
```gherkin
CRITERIO-4.1: Registro de auditoría post-asignación
  Dado que:  Un turno fue asignado exitosamente a un médico
  Cuando:    Se consulta el log de auditoría para ese turno
  Entonces:  Existe al menos un registro con event_type "APPOINTMENT_ASSIGNED"
  Y:         Contiene appointment_id del turno
  Y:         Contiene doctor_id del médico asignado
  Y:         Contiene office_number del consultorio
  Y:         Contiene timestamp en formato ISO 8601
```

**Error Path**
```gherkin
CRITERIO-4.2: Sin auditoría si no hay asignación
  Dado que:  Un turno permanece en estado "waiting" sin asignación
  Cuando:    Se consulta el log de auditoría para ese turno
  Entonces:  No existe registro con event_type "APPOINTMENT_ASSIGNED" para ese turno
```

---

### Reglas de Negocio Validadas
1. **Urgencia obligatoria**: Solo Alta, Media, Baja. Sin urgencia → HTTP 400.
2. **Ordenamiento de cola**: Urgencia (Alta=3 > Media=2 > Baja=1) → luego FIFO (`created_at` ASC).
3. **Disponibilidad de médico**: Solo estado `available` permite asignación.
4. **Sin médico → sin asignación**: El turno permanece en `waiting`, no se fuerza asignación.
5. **Idempotencia**: Turno ya asignado no se reasigna (consultas posteriores mantienen estado).
6. **Auditoría**: Toda asignación genera registro auditable inmutable.
7. **Latencia SLA**: Asignación + notificación ≤2s en condiciones normales (validado con polling ≤10s en tests por tolerancia).

---

## 2. DISEÑO

### Endpoints Bajo Prueba

#### POST /turnos (existente — extender con urgency)
- **Descripción**: Registra un turno con urgencia
- **Auth**: Sí (Bearer token del signUp)
- **Request Body**:
  ```json
  {
    "cedula": 123456,
    "nombre": "Juan García",
    "priority": "alta"
  }
  ```
- **Response 202**:
  ```json
  {
    "id": "uuid",
    "cedula": 123456,
    "nombre": "Juan García",
    "priority": "alta",
    "state": "waiting",
    "created_at": "2026-04-08T14:30:00Z"
  }
  ```
- **Response 400**: Urgencia faltante o inválida

#### POST /turnos (sin urgencia — flujo negativo)
- **Request Body**:
  ```json
  {
    "cedula": 123456,
    "nombre": "Juan García"
  }
  ```
- **Response 400**:
  ```json
  {
    "message": "Urgencia es obligatoria. Valores: Alta, Media, Baja"
  }
  ```

#### GET /turnos/queue/position/{appointment_id}
- **Descripción**: Posición de un turno en la cola
- **Auth**: No (público)
- **Response 200** (en cola):
  ```json
  {
    "id": "uuid",
    "state": "waiting",
    "urgency": "Alta",
    "position": 3,
    "total_in_queue": 8,
    "created_at": "2026-04-08T14:30:00Z"
  }
  ```
- **Response 200** (asignado):
  ```json
  {
    "id": "uuid",
    "state": "assigned",
    "position": null,
    "assigned_doctor_name": "Dr. García López",
    "assigned_office_number": 5,
    "assigned_at": "2026-04-08T14:35:00Z"
  }
  ```
- **Response 404**: Turno inexistente

#### GET /turnos/queue
- **Descripción**: Cola completa ordenada por urgencia + FIFO
- **Auth**: Sí (roles: admin, receptionist)
- **Response 200**:
  ```json
  {
    "total": 3,
    "items": [
      { "id": "uuid-1", "position": 1, "nombre": "Paciente2", "urgency": "Alta" },
      { "id": "uuid-2", "position": 2, "nombre": "Paciente3", "urgency": "Media" },
      { "id": "uuid-3", "position": 3, "nombre": "Paciente1", "urgency": "Baja" }
    ]
  }
  ```

#### GET /turnos/{id}
- **Descripción**: Detalle de un turno (incluye asignación si existe)
- **Auth**: No (público)
- **Response 200**:
  ```json
  {
    "id": "uuid",
    "cedula": 123456,
    "nombre": "Juan García",
    "priority": "alta",
    "state": "assigned",
    "assigned_doctor_name": "Dr. García López",
    "assigned_office_number": 5,
    "assigned_at": "2026-04-08T14:35:00Z",
    "created_at": "2026-04-08T14:30:00Z"
  }
  ```

#### GET /audit (operacional)
- **Descripción**: Log de auditoría filtrable por appointment_id
- **Auth**: Sí (admin)
- **Query Params**: `appointment_id`
- **Response 200**:
  ```json
  [
    {
      "event_type": "APPOINTMENT_ASSIGNED",
      "appointment_id": "uuid",
      "doctor_id": "doctor-uuid",
      "office_number": 5,
      "timestamp": "2026-04-08T14:35:00Z"
    }
  ]
  ```

---

### Arquitectura Screenplay — Archivos Nuevos

#### Feature Files (Gherkin)
| Feature | Archivo | Escenarios | Tags |
|---------|---------|------------|------|
| Registro con urgencia | `features/hu03_registro_urgencia.feature` | 3 (happy + 2 error) | `@hu03`, `@registro`, `@urgencia` |
| Posición en cola | `features/hu03_posicion_cola.feature` | 4 (happy + error + edge) | `@hu03`, `@cola`, `@posicion` |
| Asignación de médico | `features/hu03_asignacion_medico.feature` | 4 (happy + error + edge) | `@hu03`, `@asignacion`, `@critical` |
| Auditoría | `features/hu03_auditoria.feature` | 2 (happy + error) | `@hu03`, `@auditoria` |

#### Tasks (Screenplay)
| Task | Archivo | Verbo HTTP | Endpoint |
|------|---------|-----------|----------|
| `CreateTurnoWithUrgency` | `tasks/CreateTurnoWithUrgency.java` | POST | `/turnos` |
| `CreateTurnoWithoutUrgency` | `tasks/CreateTurnoWithoutUrgency.java` | POST | `/turnos` |
| `GetQueuePosition` | `tasks/GetQueuePosition.java` | GET | `/turnos/queue/position/{id}` |
| `GetFullQueue` | `tasks/GetFullQueue.java` | GET | `/turnos/queue` |
| `GetTurnoById` | `tasks/GetTurnoById.java` | GET | `/turnos/{id}` |
| `GetAuditLog` | `tasks/GetAuditLog.java` | GET | `/audit?appointment_id={id}` |
| `WaitForAssignment` | `tasks/WaitForAssignment.java` | GET (polling) | `/turnos/{id}` — poll hasta `state=assigned` o timeout |

#### Questions (Screenplay)
| Question | Archivo | Qué responde |
|----------|---------|-------------|
| `QueuePosition` | `questions/QueuePosition.java` | Posición numérica en la cola |
| `TurnoState` | `questions/TurnoState.java` | Estado actual del turno (waiting/assigned/completed) |
| `AssignedDoctorName` | `questions/AssignedDoctorName.java` | Nombre del médico asignado |
| `AssignedOfficeNumber` | `questions/AssignedOfficeNumber.java` | Número de consultorio asignado |
| `TurnoUrgency` | `questions/TurnoUrgency.java` | Nivel de urgencia del turno |
| `QueueOrder` | `questions/QueueOrder.java` | Lista ordenada de nombres en la cola |
| `AuditEventExists` | `questions/AuditEventExists.java` | Boolean: si existe evento de auditoría para el turno |
| `ResponseBody` | `questions/ResponseBody.java` | Body completo como JsonObject |

#### Step Definitions
| Step Def | Archivo | Features que cubre |
|----------|---------|-------------------|
| `RegistroUrgenciaStepDefs` | `stepdefs/RegistroUrgenciaStepDefs.java` | `hu03_registro_urgencia.feature` |
| `PosicionColaStepDefs` | `stepdefs/PosicionColaStepDefs.java` | `hu03_posicion_cola.feature` |
| `AsignacionMedicoStepDefs` | `stepdefs/AsignacionMedicoStepDefs.java` | `hu03_asignacion_medico.feature` |
| `AuditoriaStepDefs` | `stepdefs/AuditoriaStepDefs.java` | `hu03_auditoria.feature` |

#### Utilities
| Clase | Archivo | Responsabilidad |
|-------|---------|-----------------|
| `TestConstants` | `utils/TestConstants.java` | **MODIFICAR** — añadir constantes: endpoints nuevos, payloads, urgencies |
| `PollingHelper` | `utils/PollingHelper.java` | **NUEVO** — utility para polling con timeout configurable (WaitForAssignment) |

---

### Diseño del Polling (WaitForAssignment)

El motor de asignación es asíncrono (RabbitMQ → Consumer → MongoDB). Para validar la asignación vía API REST se usa **polling con timeout**:

```
┌──────────────────────────────────────────────┐
│ WaitForAssignment Task                       │
│                                              │
│ 1. GET /turnos/{id}                          │
│ 2. ¿state == "assigned"?                     │
│    ├── SÍ → guardar response, salir          │
│    └── NO → esperar 1s, reintentar (max 10x) │
│ 3. Si timeout → guardar última response      │
└──────────────────────────────────────────────┘

Configuración:
  POLL_INTERVAL_MS = 1000
  POLL_MAX_RETRIES = 10
  POLL_TIMEOUT_MS  = 10000
```

Se usa un timeout de 10 segundos (mayor que el SLA de 2s) para absorber variabilidad en ambientes locales.

---

### Estructura Final del Proyecto

```
src/test/java/com/maestria/qa/
├── actors/
│   └── ApiTester.java                          (existente)
├── questions/
│   ├── ErrorMessage.java                       (existente)
│   ├── JwtToken.java                           (existente)
│   ├── ResponseCode.java                       (existente)
│   ├── TurnosList.java                         (existente)
│   ├── UserProfile.java                        (existente)
│   ├── QueuePosition.java                      ★ NUEVO
│   ├── TurnoState.java                         ★ NUEVO
│   ├── AssignedDoctorName.java                 ★ NUEVO
│   ├── AssignedOfficeNumber.java               ★ NUEVO
│   ├── TurnoUrgency.java                       ★ NUEVO
│   ├── QueueOrder.java                         ★ NUEVO
│   ├── AuditEventExists.java                   ★ NUEVO
│   └── ResponseBody.java                       ★ NUEVO
├── runners/
│   └── CucumberRunner.java                     (existente)
├── stepdefs/
│   ├── TurnosAuthStepDefinitions.java          (existente)
│   ├── RegistroUrgenciaStepDefs.java           ★ NUEVO
│   ├── PosicionColaStepDefs.java               ★ NUEVO
│   ├── AsignacionMedicoStepDefs.java           ★ NUEVO
│   └── AuditoriaStepDefs.java                  ★ NUEVO
├── tasks/
│   ├── CreateTurno.java                        (existente)
│   ├── GetAllTurnos.java                       (existente)
│   ├── GetTurnosByCedula.java                  (existente)
│   ├── SignUp.java                             (existente)
│   ├── CreateTurnoWithUrgency.java             ★ NUEVO
│   ├── CreateTurnoWithoutUrgency.java          ★ NUEVO
│   ├── GetQueuePosition.java                   ★ NUEVO
│   ├── GetFullQueue.java                       ★ NUEVO
│   ├── GetTurnoById.java                       ★ NUEVO
│   ├── GetAuditLog.java                        ★ NUEVO
│   └── WaitForAssignment.java                  ★ NUEVO
└── utils/
    ├── ApiConfig.java                          (existente)
    ├── RestContext.java                         (existente)
    ├── SerenityReportGenerator.java            (existente)
    ├── TestConstants.java                      (existente — MODIFICAR)
    └── PollingHelper.java                      ★ NUEVO

src/test/resources/features/
├── turnos.feature                              (existente)
├── hu03_registro_urgencia.feature              ★ NUEVO
├── hu03_posicion_cola.feature                  ★ NUEVO
├── hu03_asignacion_medico.feature              ★ NUEVO
└── hu03_auditoria.feature                      ★ NUEVO
```

---

### Métodos HTTP Implementados (nuevos)

| Verbo | Endpoint | Task | Escenario |
|-------|----------|------|-----------|
| POST | `/turnos` (con urgency) | `CreateTurnoWithUrgency` | Registro con urgencia |
| POST | `/turnos` (sin urgency) | `CreateTurnoWithoutUrgency` | Flujo negativo |
| GET | `/turnos/queue/position/{id}` | `GetQueuePosition` | Consulta posición |
| GET | `/turnos/queue` | `GetFullQueue` | Cola completa ordenada |
| GET | `/turnos/{id}` | `GetTurnoById` | Detalle de turno |
| GET | `/audit?appointment_id={id}` | `GetAuditLog` | Consulta auditoría |
| GET | `/turnos/{id}` (polling) | `WaitForAssignment` | Esperar asignación |

**Total verbos HTTP**: 2 POST + 5 GET = 7 llamadas distintas  
**Total escenarios**: 13 (11 happy/error + 2 edge case)

---

## 3. LISTA DE TAREAS

### Fase 1: Infraestructura y Utils

- [ ] **T1** (1h) Modificar `TestConstants.java` — añadir constantes para nuevos endpoints, payloads de urgencia y configuración de polling
  ```java
  // Nuevas constantes:
  Api.QUEUE_POSITION_ENDPOINT = "/turnos/queue/position"
  Api.QUEUE_ENDPOINT = "/turnos/queue"
  Api.AUDIT_ENDPOINT = "/audit"
  Payload.URGENCY = "priority"
  Payload.STATE = "state"
  Payload.POSITION = "position"
  Payload.ASSIGNED_DOCTOR_NAME = "assigned_doctor_name"
  Payload.ASSIGNED_OFFICE_NUMBER = "assigned_office_number"
  Urgency.ALTA = "alta"
  Urgency.MEDIA = "media"
  Urgency.BAJA = "baja"
  Polling.INTERVAL_MS = 1000
  Polling.MAX_RETRIES = 10
  Polling.TIMEOUT_MS = 10000
  ```

- [ ] **T2** (1h) Crear `PollingHelper.java` — utility genérico para polling con predicate + timeout + interval configurable

### Fase 2: Tasks Screenplay

- [ ] **T3** (1h) Crear `CreateTurnoWithUrgency.java` — POST `/turnos` con cedula, nombre, priority; factory method `.forPatient(cedula, nombre, urgency)`
- [ ] **T4** (30min) Crear `CreateTurnoWithoutUrgency.java` — POST `/turnos` sin campo priority (flujo negativo)
- [ ] **T5** (1h) Crear `GetQueuePosition.java` — GET `/turnos/queue/position/{id}`; factory `.forTurno(appointmentId)`
- [ ] **T6** (1h) Crear `GetFullQueue.java` — GET `/turnos/queue`; factory `.fromTheApi()`
- [ ] **T7** (30min) Crear `GetTurnoById.java` — GET `/turnos/{id}`; factory `.withId(turnoId)`
- [ ] **T8** (1h) Crear `GetAuditLog.java` — GET `/audit?appointment_id={id}`; factory `.forAppointment(appointmentId)`
- [ ] **T9** (2h) Crear `WaitForAssignment.java` — polling GET `/turnos/{id}` hasta `state=assigned` o timeout; usa `PollingHelper`

### Fase 3: Questions Screenplay

- [ ] **T10** (30min) Crear `QueuePosition.java` — extrae `position` de la response como Integer
- [ ] **T11** (30min) Crear `TurnoState.java` — extrae `state` como String
- [ ] **T12** (30min) Crear `AssignedDoctorName.java` — extrae `assigned_doctor_name` como String
- [ ] **T13** (30min) Crear `AssignedOfficeNumber.java` — extrae `assigned_office_number` como Integer
- [ ] **T14** (30min) Crear `TurnoUrgency.java` — extrae `priority`/`urgency` como String
- [ ] **T15** (1h) Crear `QueueOrder.java` — extrae lista ordenada de nombres de `items[].nombre`
- [ ] **T16** (1h) Crear `AuditEventExists.java` — Boolean: busca evento `APPOINTMENT_ASSIGNED` en array de auditoría
- [ ] **T17** (30min) Crear `ResponseBody.java` — body completo como String (utility genérico)

### Fase 4: Feature Files (Gherkin)

- [ ] **T18** (1h) Crear `hu03_registro_urgencia.feature` — 3 escenarios: happy path + sin urgencia + urgencia inválida
- [ ] **T19** (1.5h) Crear `hu03_posicion_cola.feature` — 4 escenarios: posición, ordenamiento, turno inexistente, turno asignado
- [ ] **T20** (2h) Crear `hu03_asignacion_medico.feature` — 4 escenarios: asignación exitosa, prioridad, sin médico, FIFO
- [ ] **T21** (1h) Crear `hu03_auditoria.feature` — 2 escenarios: auditoría post-asignación, sin auditoría sin asignación

### Fase 5: Step Definitions

- [ ] **T22** (2h) Crear `RegistroUrgenciaStepDefs.java` — steps para registro con/sin urgencia; usa `CreateTurnoWithUrgency`, `CreateTurnoWithoutUrgency`, `ResponseCode`, `TurnoState`, `TurnoUrgency`, `ErrorMessage`
- [ ] **T23** (2h) Crear `PosicionColaStepDefs.java` — steps para consulta de posición y cola; usa `GetQueuePosition`, `GetFullQueue`, `QueuePosition`, `QueueOrder`, `TurnoState`
- [ ] **T24** (3h) Crear `AsignacionMedicoStepDefs.java` — steps para asignación + polling; usa `WaitForAssignment`, `AssignedDoctorName`, `AssignedOfficeNumber`, `TurnoState`, `CreateTurnoWithUrgency`
- [ ] **T25** (2h) Crear `AuditoriaStepDefs.java` — steps para verificar auditoría; usa `GetAuditLog`, `AuditEventExists`

### Fase 6: Integración y Ejecución

- [ ] **T26** (30min) Verificar `CucumberRunner.java` — confirmar que el glue path incluye los nuevos Step Defs  
  (Ya está configurado con `com.maestria.qa.stepdefs`, los nuevos archivos se detectan automáticamente)
- [ ] **T27** (1h) Ejecutar toda la suite: `gradle test -Dcucumber.filter.tags="@hu03"` — verificar 13 escenarios
- [ ] **T28** (30min) Ejecutar suite completa: `gradle test` — verificar que los 2 escenarios existentes + 13 nuevos pasan (15 total)
- [ ] **T29** (30min) Verificar reporte Serenity HTML en `target/site/serenity/index.html`

### Resumen de Entregables

| Tipo | Archivos Nuevos | Archivos Modificados |
|------|----------------|---------------------|
| Features | 4 | 0 |
| Tasks | 7 | 0 |
| Questions | 8 | 0 |
| Step Definitions | 4 | 0 |
| Utils | 1 | 1 (`TestConstants.java`) |
| **Total** | **24 archivos nuevos** | **1 archivo modificado** |

| Escenarios | Tags | Tipo |
|-----------|------|------|
| 13 nuevos | `@hu03` | 7 happy path + 4 error + 2 edge |
| 2 existentes | `@smoke`, `@negative` | Sin cambios |
| **15 total** | — | — |

---

### Criterios de Completitud

- [ ] 13 escenarios nuevos pasan con `gradle test -Dcucumber.filter.tags="@hu03"`
- [ ] Los 2 escenarios existentes siguen pasando (no regresión)
- [ ] Reporte Serenity HTML se genera correctamente con los 15 escenarios
- [ ] Todas las Tasks siguen el patrón Screenplay existente (factory method + `performAs`)
- [ ] Todas las Questions siguen el patrón existente (factory method + `answeredBy`)
- [ ] `RestContext` se usa como ThreadLocal compartido para responses
- [ ] Nomenclatura semántica: clases en PascalCase, métodos en camelCase, constantes en UPPER_SNAKE_CASE
- [ ] Zero cambios a archivos existentes (excepto `TestConstants.java`)

---

## 4. DEPENDENCIAS Y PRECONDICIONES

### Precondiciones para Ejecución
1. **API backend corriendo** en `http://localhost:3000` con los endpoints de HU-03 implementados
2. **Al menos un médico** registrado con estado `available` en la base de datos
3. **MongoDB** accesible para persistencia de turnos y auditoría
4. **RabbitMQ** corriendo para el flujo asíncrono de asignación
5. **Docker Compose** con 5 servicios levantados: producer, consumer, frontend, rabbitmq, mongodb

### Dependencias de Build
No se requieren nuevas dependencias en `build.gradle`. El stack existente cubre:
- Serenity BDD 4.1.5 → Screenplay + REST
- Cucumber 7.14.0 → Gherkin parser
- REST Assured 5.3.2 → HTTP client
- Gson 2.10.1 → JSON payloads
- JUnit 4 → Test runner

---

**Próximo paso**: Cambiar `status: DRAFT` a `status: APPROVED` y ejecutar `/implement-backend` para crear los 24 archivos nuevos.
