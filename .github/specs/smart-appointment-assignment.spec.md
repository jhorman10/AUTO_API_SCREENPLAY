---
id: SPEC-003
status: DRAFT
feature: smart-appointment-assignment
created: 2026-04-08
updated: 2026-04-08
author: spec-generator
version: "1.0"
related-specs: []
---

# Spec: Sistema Inteligente de Asignación de Turnos Médicos

> **Estado:** `DRAFT` → Cambiar a `APPROVED` antes de iniciar implementación.  
> **Ciclo de Vida:** DRAFT → APPROVED → IN_PROGRESS → IMPLEMENTED

---

## 1. REQUERIMIENTOS

### Descripción
Sistema que reemplaza la asignación aleatoria de turnos médicos por un motor inteligente basado en urgencia del paciente y disponibilidad real del médico. Los pacientes ven su posición en cola en tiempo real y reciben notificación inmediata (<2s) cuando se les asigna un médico disponible, eliminando la experiencia de ser enviado a consultorios vacíos.

### Requerimiento de Negocio

Iniciativa de tres historias de usuario que implementan:
1. **HU-01**: Registro de urgencia en turnos
2. **HU-02**: Visualización de posición en cola en tiempo real
3. **HU-03**: Notificación inmediata de asignación de médico

Soportadas por 6 historias técnicas (HT-01 → HT-06) en dos sprints.

---

## Historias de Usuario

### HU-01: Registro de Urgencia

```
Como:        Recepcionista
Quiero:      Registrar un paciente con nivel de urgencia (Alta, Media, Baja)
Para:        Que el sistema priorice la atención según criticidad clínica

Prioridad:   Media
Estimación:  3 puntos
Dependencias: HT-01, HT-02
Capa:        Backend + Frontend
```

#### Criterios de Aceptación — HU-01

**CRITERIO-1.1: Registro exitoso con urgencia válida**
```gherkin
DADO QUE:    Existe un formulario de registro con field "urgency" en dropdown
CUANDO:      La recepcionista selecciona urgencia "Alta" y submite el formulario
ENTONCES:    El turno se guarda en state "waiting" con urgency="Alta" 
             Y server responde HTTP 201 con el turno creado
             Y el turno contiene: id, urgency, state, created_at
```

**CRITERIO-1.2: Rechazo sin urgencia válida**
```gherkin
DADO QUE:    El formulario requiere urgencia obligatoria
CUANDO:      El usuario intenta enviar sin seleccionar urgencia
ENTONCES:    Server responde HTTP 400 con mensaje "Urgencia es obligatoria"
             Y el registro NO se persiste
```

**CRITERIO-1.3: Solo valores permitidos**
```gherkin
DADO QUE:    El sistema solo acepta: Alta, Media, Baja
CUANDO:      Alguien intenta registrar con urgency="Crítica" (fuera del conjunto)
ENTONCES:    Server responde HTTP 400 "Urgencia inválida. Valores: Alta, Media, Baja"
```

### HU-02: Visualización de Posición

```
Como:        Usuario en sala de espera
Quiero:      Ver en tiempo real mi posición en la cola
Para:        Conocer mi progreso sin preguntar en recepción

Prioridad:   Media
Estimación:  8 puntos
Dependencias: HT-02 (consulta), HT-03 (WebSocket)
Capa:        Backend + Frontend
```

#### Criterios de Aceptación — HU-02

**CRITERIO-2.1: Visualización inicial de posición**
```gherkin
DADO QUE:    Un paciente llega a pantalla de espera tras registrarse
CUANDO:      La pantalla carga
ENTONCES:    Muestra "Posición en cola: 3 de 12"
             Y muestra estado actual: "waiting"
             Y muestra hora de registro
```

**CRITERIO-2.2: Actualización en tiempo real sin recarga**
```gherkin
DADO QUE:    Un paciente ve su posición como "5"
CUANDO:      Otro paciente es llamado (transición a atendido)
ENTONCES:    La pantalla actualiza automáticamente la posición a "4"
             SIN refrescar, gracias a WebSocket
             Y muestra animación sutil de cambio (opcional visual)
```

**CRITERIO-2.3: Reconexión automática**
```gherkin
DADO QUE:    La conexión WebSocket se pierde
CUANDO:      El cliente detecta desconexión
ENTONCES:    Intenta reintentar conexión automáticamente cada 3 segundos
             Y muestra estado "Reconectando..." en pantalla
             Y preserva último dato conocido (posición: 5)
             Y una vez recuperada la conexión, sincroniza estado actual
```

**CRITERIO-2.4: Consistencia si turno no existe o no está activo**
```gherkin
DADO QUE:    Un turno fue cancelado o completado
CUANDO:      El paciente recarga o se reconecta
ENTONCES:    Pantalla muestra estado final "Completado" o "Cancelado"
             Y no intenta mostrar posición
```

### HU-03: Notificación de Asignación de Médico

```
Como:        Paciente
Quiero:      Recibir notificación inmediata cuando mi turno sea asignado
Para:        Confirmar que seré atendido y saber a qué consultorio dirigirme

Prioridad:   Alta
Estimación:  13 puntos
Dependencias: HT-04, HT-05, HT-06
Capa:        Backend + Frontend
```

#### Criterios de Aceptación — HU-03

**CRITERIO-3.1: Notificación dentro de 2 segundos (happy path)**
```gherkin
DADO QUE:    Un médico está disponible y hay pacientes en cola
CUANDO:      El sistema ejecuta el motor de asignación (proceso scheduler o evento)
             Y asigna un turno al médico
Y publica AppointmentAssigned event
ENTONCES:    El paciente recibe notificación en pantalla en ≤2 segundos
             Y notificación contiene:
               - Nombre del médico
               - Número de consultorio
               - Mensaje: "Ya estamos listos para atenderte con Dr. García en consultorio 5"
             Y estado del turno transiciona a "assigned"
```

**CRITERIO-3.2: Sin notificación si turno no está asignado**
```gherkin
DADO QUE:    Un turno permanece en estado "waiting" (no hay médico disponible)
CUANDO:      El paciente espera en pantalla
ENTONCES:    NO recibe notificación de llamado
             Y sigue viendo posición en cola: "2 de 8"
```

**CRITERIO-3.3: Auditoría de cada notificación**
```gherkin
DADO QUE:    Se emite una notificación de asignación
CUANDO:      La notificación se envía al paciente
ENTONCES:    Se registra en log de auditoría:
               - appointment_id
               - timestamp_iso8601
               - doctor_id
               - office_number
               - event_type: "APPOINTMENT_ASSIGNED"
               - delivered: true/false
```

**CRITERIO-3.4: Manejo de error sin bloquear paciente**
```gherkin
DADO QUE:    La entrega de notificación falla temporalmente
CUANDO:      Se reintenta envío (retry: 3x máximo)
ENTONCES:    Se registra en DLQ si se agota retry
             Y el turno mantiene state="assigned" (ya persistido)
             Y el médico ya puede atender al paciente
             (Notificación es best-effort, no bloquea asignación)
```

---

## Reglas de Negocio

### RN-01: Validación de Urgencia
- Campo `urgency` acepta solo: **Alta**, **Media**, **Baja** (case-insensitive en entrada, guardado en mayúsculas)
- Urgencia es obligatoria en registro
- Violación → HTTP 400 + mensaje claro

### RN-02: Ordenamiento de Cola
- **Criterio primario**: `urgency` (Alta > Media > Baja, numérico: 3, 2, 1)
- **Criterio secundario**: `created_at` (ascendente — FIFO en misma urgencia)
- Ejemplo: 2 pacientes "Alta", 3 pacientes "Media"  
  → Orden: [Alta#1, Alta#2, Media#1, Media#2, Media#3]

### RN-03: Disponibilidad de Médico
- Estado `available` = médico marcó check-in y no está ocupado (`busy=false`)
- Solo asihar turnos a doctor en estado `available`
- Si no hay doctor disponible, turno permanece en `waiting`

### RN-04: Idempotencia de Asignación
- Una vez asignado (state=assigned), no debe reasignarse aunque el evento se reintente
- Usa transacción atómica o versión de documento para evitar race condition

### RN-05: Latencia Extremo a Extremo
- Consumer publica evento AppointmentAssigned
- Producer consume en notifications_queue
- Producer broadcast vía WebSocket al paciente
- **SLA**: ≤2 segundos en condiciones normales (99% de casos)

### RN-06: Auditoría Inmutable
- Cada decisión de asignación se registra en `audit_log`
- Contiene: timestamp, appointment_id, doctor_id, office_number, reason, input_data
- No puede ser modificada, solo leída (append-only)

---

## 2. DISEÑO

### Modelos de Datos

#### Entidades Afectadas
| Entidad | Almacén | Cambios | Descripción |
|---------|---------|---------|-------------|
| `Appointment` | `appointments` | **MODIFICADA** | Añadir campo `urgency` (enum) + transiciones de estado ampliadas |
| `Doctor` | `doctors` | Existente | Campo `status` ya existe; se valida en asignación |
| `Office` | `offices` | Existente | Se consulta en asignación |
| `AuditLog` | `audit_logs` | **NUEVA** (opcional) | Registros de decisiones de asignación |

#### Campos del Modelo `Appointment`

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `id` / `appointment_id` | UUID (branded) | sí | auto-generado | Identificador único |
| `patient_name` | string | sí | max 200 chars | Nombre paciente |
| `patient_id_card` | string (branded: IdCard) | sí | cédula válida | Cédula del paciente |
| `urgency` | enum | sí | Alta \| Media \| Baja | Nivel de prioridad |
| `state` | enum | sí | waiting \| assigned \| completed \| cancelled | Estado del turno |
| `assigned_doctor_id` | UUID (branded) | no | FK doctors.id | Médico asignado (null si waiting) |
| `assigned_office_number` | number | no | 1-100 | Consultorio asignado (null si waiting) |
| `created_at` | datetime (UTC) | sí | auto-generado | Timestamp creación |
| `updated_at` | datetime (UTC) | sí | auto-generado | Timestamp última actualización |
| `assigned_at` | datetime (UTC) | no | auto-generado-on-assign | Timestamp de asignación |
| `completed_at` | datetime (UTC) | no | auto-generado-on-complete | Timestamp de completación |

#### Índices Recomendados
| Índice | Campos | Razón | Tipo |
|--------|--------|-------|------|
| `idx_urgency_created` | `(urgency, created_at)` | Ordenamiento de cola |  |
| `idx_state_urgency` | `(state, urgency)` | Filtros de cola activa |  |
| `idx_doctor_state` | `(assigned_doctor_id, state)` | Turnos por doctor |  |
| `idx_created_at_ttl` | `created_at` | TTL cleanup opcional | TTL 90 días |

### API Endpoints

#### POST /api/v1/appointments/register
**Descripción**: Crea un nuevo turno con urgencia  
**Auth requerida**: Sí (roles: receptionist, admin)  
**Content-Type**: application/json

**Request Body**:
```json
{
  "patient_name": "Juan García",
  "patient_id_card": "1234567890",
  "urgency": "Alta"
}
```

**Response 201**:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "patient_name": "Juan García",
  "patient_id_card": "1234567890",
  "urgency": "Alta",
  "state": "waiting",
  "created_at": "2026-04-08T14:30:00Z",
  "updated_at": "2026-04-08T14:30:00Z",
  "assigned_doctor_id": null,
  "assigned_office_number": null
}
```

**Response 400**:
```json
{
  "error": "Bad Request",
  "message": "Urgencia es obligatoria. Valores: Alta, Media, Baja"
}
```

**Response 409**:
```json
{
  "error": "Conflict",
  "message": "Ya existe un turno activo para esta cédula. Espere a que se complete o cancele."
}
```

---

#### GET /api/v1/appointments/queue/position/{appointment_id}
**Descripción**: Obtiene posición actual en cola y estado del turno  
**Auth requerida**: No (pública)  
**Query Params**: 
- `appointment_id` (path) — UUID del turno  

**Response 200** (si está en cola activa):
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "state": "waiting",
  "urgency": "Media",
  "position": 5,
  "total_in_queue": 12,
  "position_text": "Posición 5 de 12",
  "estimated_time_minutes": 8,
  "created_at": "2026-04-08T14:30:00Z"
}
```

**Response 200** (si está asignado):
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "state": "assigned",
  "urgency": "Media",
  "position": null,
  "assigned_doctor_name": "Dr. García López",
  "assigned_office_number": 5,
  "assigned_at": "2026-04-08T14:35:00Z",
  "message": "Ya estamos listos para atenderte con Dr. García López en consultorio 5"
}
```

**Response 404**:
```json
{
  "error": "Not Found",
  "message": "Turno no encontrado"
}
```

---

#### GET /api/v1/appointments/queue (admin/operacional)
**Descripción**: Lista completa de cola ordenada por urgencia + FIFO  
**Auth requerida**: Sí (roles: admin, receptionist)  
**Query Params**:
- `limit` (default 50)
- `offset` (default 0)

**Response 200**:
```json
{
  "total": 12,
  "items": [
    {
      "id": "550e8400...",
      "position": 1,
      "patient_name": "Juan García",
      "urgency": "Alta",
      "created_at": "2026-04-08T14:20:00Z"
    },
    {
      "id": "660e8400...",
      "position": 2,
      "patient_name": "María López",
      "urgency": "Alta",
      "created_at": "2026-04-08T14:25:00Z"
    },
    {
      "id": "770e8400...",
      "position": 3,
      "patient_name": "Carlos Ruiz",
      "urgency": "Media",
      "created_at": "2026-04-08T14:15:00Z"
    }
  ]
}
```

---

### WebSocket Channels

#### Namespace: `/ws/appointments` (Público - Pantalla de Espera)

**Propósito**: Notificar cambios de posición en cola y asignación a pacientes  
**Auth**: Token estático configurado (no autenticación Firebase)  
**Auto-reconexión**: Cliente reintentar cada 3 segundos si desconecta

**Eventos que Emite Server → Client**:

1. **`queue:position_updated`**
   ```json
   {
     "appointment_id": "550e8400...",
     "position": 4,
     "total_in_queue": 12,
     "updated_at": "2026-04-08T14:40:00Z"
   }
   ```
   Enviado cuando un turno asignado salió de la cola o llegó uno nuevo.

2. **`appointment:assigned`**
   ```json
   {
     "appointment_id": "550e8400...",
     "doctor_name": "Dr. García López",
     "office_number": 5,
     "urgency": "Media",
     "message": "Ya estamos listos para atenderte con Dr. García López en consultorio 5",
     "assigned_at": "2026-04-08T14:42:00Z"
   }
   ```
   Enviado cuando se asigna un turno. **Latencia objetivo**: <2 segundos.

3. **`connection:status`**
   ```json
   {
     "status": "connected" | "reconnecting" | "error",
     "message": "Reconectando...",
     "attempt": 3
   }
   ```
   Informar estado de conexión al cliente.

**Eventos que Recibe Client → Server**:

- `subscribe:appointment` → client pasa `{ appointment_id: "..." }` para escuchar cambios específicos

---

### Componentes Frontend

#### Páginas Nuevas / Modificadas

| Página | Ruta | Componentes Clave | Cambios |
|--------|------|------------------|---------|
| Espera Pública | `/public/queue` | `QueuePositionCard`, `AssignmentNotification`, `WebSocketStatus` | NUEVA — Pantalla pública de espera |
| Registro (Recepcionista) | `/appointments/register` | `AppointmentRegistrationForm` | MODIFICADA — Añadir selector de urgencia |

#### Componentes Nuevos / Modificados

| Componente | Archivo | Props | Descripción | Cambios |
|------------|---------|-------|-------------|---------|
| `QueuePositionCard` | `components/QueuePositionCard.tsx` | `appointmentId`, `onPositionUpdate` | Tarjeta que muestra posición en cola | NUEVO |
| `AssignmentNotification` | `components/AssignmentNotification.tsx` | `assignment`, `onDismiss` | Notificación modal cuando se asigna | NUEVO |
| `WebSocketStatus` | `components/WebSocketStatus.tsx` | `status`, `lastUpdate` | Indicador visual de conexión WS | NUEVO |
| `UrgencySelector` | `components/UrgencySelector.tsx` | `value`, `onChange` | Dropdown urgency (Alta, Media, Baja) | NUEVO |
| `AppointmentRegistrationForm` | `components/AppointmentRegistrationForm.tsx` | `onSubmit` | Formulario de registro | MODIFICADO — Integra UrgencySelector |

#### Custom Hooks Nuevos / Modificados

| Hook | Ubicación | Responsabilidad |
|------|-----------|-----------------|
| `useQueuePosition` | `hooks/useQueuePosition.ts` | Consulta GET position + polling / WS |
| `useAppointmentAssignment` | `hooks/useAppointmentAssignment.ts` | Escucha evento assignment vía WS, muestra notificación |
| `useWebSocketReconnection` | `hooks/useWebSocketReconnection.ts` | Maneja reconexión automática, preserva último estado |

---

### Domain Layer (Backend)

#### Value Objects
| VO | Archivo | Campos | Validación |
|---|---------|--------|-----------|
| `Urgency` | `domain/value-objects/urgency.value-object.ts` | `HIGH`, `MEDIUM`, `LOW` | Set cerrado, numérico para ordenamiento |
| `AppointmentId` | (existente) | branded UUID | — |
| `IdCard` | (existente) | branded string | — |

#### Entities
| Entity | Cambios | Métodos Nuevos |
|--------|---------|----------------|
| `Appointment` | Añadir `urgency?: Urgency`, `state` transiciones extendidas | `assignDoctor()`, `complete()`, `cancel()` |
| `Doctor` | Existente | (no cambios) |

#### Use Cases (Application Layer / Backend)
| Use Case | Archivo | Entrada | Salida | Responsabilidad |
|----------|---------|---------|--------|-----------------|
| `RegisterAppointmentUseCase` | `application/use-cases/register-appointment.use-case.ts` | RegisterAppointmentCommand | Appointment | Validar, persistir, publicar AppointmentRegisteredEvent |
| `QueryQueuePositionUseCase` | `application/use-cases/query-queue-position.use-case.ts` | appointment_id | QueuePositionView | Consultar posición en cola (read-only) |
| `AssignAvailableOfficesUseCase` | (existente, mejorar) | — | — | Seleccionar siguiente turno por urgencia, asignar médico disponible, publicar AppointmentAssignedEvent |
| `ProcessAppointmentAssignmentUseCase` | `application/use-cases/process-appointment-assignment.use-case.ts` | AppointmentAssignedEvent | void | Consumir evento y persist state |

#### Domain Events
| Evento | Publicador | Manejadores | Cambios |
|--------|-----------|------------|---------|
| `AppointmentRegisteredEvent` | RegisterAppointmentUseCase | Logger, QA | EXISTENTE — se mantiene |
| `AppointmentAssignedEvent` | AssignAvailableOfficesUseCase | Logger, NotificationBroadcaster, AuditLog | EXISTENTE — se mejora para incluir doctor_id, office, urgency |
| `QueuePositionChangedEvent` | AssignAvailableOfficesUseCase | WebSocketBroadcaster | NUEVO — para notificaciones en tiempo real |

#### Ports (Interfaces)

**Inbound** (Casos de Uso):
- `RegisterAppointmentUseCase` — mantenida
- `QueryQueuePositionUseCase` — nueva
- `AssignAvailableOfficesUseCase` — mejorada

**Outbound**:
- `AppointmentRepository` (Read + Write) — mejorado con `findQueueOrderedByUrgency()`
- `DoctorRepository` (Read) — mejorado con `findAvailableDoctors()`
- `NotificationPort` — mejorado para broadcast assignments
- `AuditPort` — nueva, para registrar decisiones

---

## 3. LISTA DE TAREAS

### Sprint 1: Fundamentos (16 puntos)

#### Backend
- [ ] **T1.1** (2h) Crear Value Object `Urgency` con validación (Alta, Media, Baja)
- [ ] **T1.2** (4h) Modificar model `Appointment` en MongoDB para incluir campo `urgency` (enum)
- [ ] **T1.3** (2h) Crear índice en MongoDB: `(urgency, created_at)` para ordenamiento eficiente
- [ ] **T1.4** (6h) Use Case `RegisterAppointmentUseCase` — validar urgency, persistir, publicar evento
- [ ] **T1.5** (4h) Use Case `QueryQueuePositionUseCase` — consulta ordenada por urgency + FIFO
- [ ] **T1.6** (3h) Repository enhancement: implementar `findQueueOrderedByUrgency()`
- [ ] **T1.7** (2h) Endpoint `POST /api/v1/appointments/register` — integra T1.4
- [ ] **T1.8** (2h) Endpoint `GET /api/v1/appointments/queue/position/{id}` — integra T1.5
- [ ] **T1.9** (2h) Tests unitarios: validación urgency y querys de cola
- [ ] **T1.10** (3h) Consumer: manejo de `AppointmentRegisteredEvent`

#### Frontend
- [ ] **T1.11** (2h) Crear componente `UrgencySelector` — dropdown Alta/Media/Baja
- [ ] **T1.12** (3h) Modificar `AppointmentRegistrationForm` — integrar UrgencySelector
- [ ] **T1.13** (2h) Crear Value Object `Urgency` (TS client-side)
- [ ] **T1.14** (2h) Tests: validación de urgency en formulario
- [ ] **T1.15** (1h) E2E: flujo completo registro con urgencia

#### Database
- [ ] **T1.16** (1h) Migration: añadir campo `urgency` a colección `appointments`
- [ ] **T1.17** (1h) Migration: crear índice `(urgency, created_at)`

#### Criteria Completitud Sprint 1
- Turnos se registran con urgency válida ✓
- Cola se puede consultar ordenada por urgency + FIFO ✓
- Tests unitarios ≥90% coverage para lógica de urgency ✓

---

### Sprint 2: Notificaciones Inteligentes (29 puntos)

#### Backend

**HT-04: Motor de Asignación**
- [ ] **T2.1** (4h) Use Case `AssignAvailableOfficesUseCase` mejorado:
  - Seleccionar turno waiting con urgency MÁS alta + created_at más antigua
  - Validar doctor available
  - Asignar doctor + office
  - Publicar `AppointmentAssignedEvent` con info completa
- [ ] **T2.2** (2h) Repository: añadir `DoctorRepository.findAvailableOrderedBy()`
- [ ] **T2.3** (2h) Policy: lógica de elegibilidad de doctor (status=available, no busy)
- [ ] **T2.4** (3h) Tests: escenarios de asignación (urgency ordering, FIFO, no doctors disponibles)

**HT-05: Publicación Confiable de Evento**
- [ ] **T2.5** (4h) Mejorar `AppointmentAssignedEvent` — incluir doctor_id, office, urgency
- [ ] **T2.6** (3h) Adapter RabbitMQ notifications: publish event con retry + DLQ
- [ ] **T2.7** (2h) Implementar idempotencia — event handler verifica que turno no esté ya asignado
- [ ] **T2.8** (3h) Tests: entrega confiable, reintentos, DLQ

**HT-06: Notificación Visible y Auditable**
- [ ] **T2.9** (3h) Domain Event Handler: `AppointmentAssignedEventHandler` → broadcast + audit
- [ ] **T2.10** (2h) Port `AuditPort` — nueva interfaz para registrar decisiones
- [ ] **T2.11** (3h) Adapter MongoDB AuditLog — persistir cada asignación con timestamp
- [ ] **T2.12** (2h) Producer Event Controller: consumir `AppointmentAssignedEvent` de RabbitMQ
- [ ] **T2.13** (2h) Adapter NotificationPort: broadcast vía WebSocket al paciente
- [ ] **T2.14** (3h) Tests: latencia (<2s en condiciones normales), auditoría, broadcast

#### Frontend

**HT-02 + 3: Real-time UI**
- [ ] **T2.15** (3h) Crear página `/public/queue` — pantalla de espera pública NUEVA
  - Formulario para buscar turno por appointment_id
  - Mostrar posición o asignación
- [ ] **T2.16** (4h) Custom Hook `useQueuePosition`:
  - GET /api/v1/appointments/queue/position/{id} inicial
  - Polling cada 5s O WS subscription (combinar)
  - Manejo de errores 404, 500
- [ ] **T2.17** (4h) Custom Hook `useWebSocketReconnection`:
  - Auto-reconnect a WebSocket cada 3s
  - Preservar último estado mientras se reconecta
  - Emitir eventos para UI
- [ ] **T2.18** (3h) Custom Hook `useAppointmentAssignment`:
  - Escuchar evento `/ws/appointments` → `appointment:assigned`
  - Display notificación modal con info doctor + office
  - Dismiss automático o manual

**Components**
- [ ] **T2.19** (2h) Componente `QueuePositionCard` — display posición / estado
- [ ] **T2.20** (2h) Componente `AssignmentNotification` — modal notificación
- [ ] **T2.21** (1h) Componente `WebSocketStatus` — badge con estado conexión
- [ ] **T2.22** (3h) Tests: componentes, hooks, integración E2E
- [ ] **T2.23** (2h) Integración en `/ws/appointments` — reconexión resiliente

#### Database
- [ ] **T2.24** (2h) Optional collection `audit_logs` con TTL 90 días
- [ ] **T2.25** (1h) Índice `(appointment_id, created_at)` en audit_logs

#### QA / Integration
- [ ] **T2.26** (4h) Gherkin scenarios: HU-01, HU-02, HU-03
- [ ] **T2.27** (3h) Test de latencia extremo a extremo: <2s
- [ ] **T2.28** (2h) Perf test: cola con 100+ turnos, query <500ms
- [ ] **T2.29** (2h) Chaos test: WebSocket desconexión + reconexión

#### Criteria Completitud Sprint 2
- Motor de asignación respeta urgency + FIFO ✓
- Evento publicado con reintentos ✓
- Notificación recibida <2s en 99% de casos ✓
- Auditoría registra cada decisión ✓
- WebSocket resiliente ante desconexiones ✓
- Tests >= 90% coverage ✓

---

## 4. RIESGOS IDENTIFICADOS

| Riesgo | Probabilidad | Impacto | Mitigación |
|--------|--------------|--------|-----------|
| Race condition en asignación de doctor | Media | Alto | Usar transacción atómica (markBusyAtomic) |
| Latencia >2s por congestión RabbitMQ | Baja | Medio | Monitoring + alertas, DLQ monitored |
| Desconexión WebSocket masiva | Baja | Medio | Reconexión automática + graceful degradation |
| Duplicación de notificación | Media | Bajo | Idempotencia event handler + versión documento |
| Datos inconsistentes urgency | Alto | Bajo | Validación en dominio + tests |

---

## 5. CRITERIOS DE ACEPTACIÓN GLOBALES

**Spec lista para pasar a APPROVED cuando**:
- [ ] Todos los criterios de aceptación (HU-01, HU-02, HU-03) están definidos con Gherkin
- [ ] Modelos de datos están finalizados y documentados
- [ ] APIs están completamente especificadas (request/response)
- [ ] Arquitectura en capas (Domain, Application, Infrastructure) definida
- [ ] Riesgos identificados y mitigación propuesta
- [ ] Estimación realista en story points (24 puntos totales: HU-01: 3, HU-02: 8, HU-03: 13)
- [ ] Product Owner aprueba todos los criterios de aceptación

---

## 6. REFERENCIAS

- **Arquitectura**: Hexagonal (Ports & Adapters) + Event-Driven + CQRS (Domain Read ≠ Writes)
- **Stack**: NestJS (producer/consumer), Next.js (frontend), MongoDB, RabbitMQ, WebSocket
- **Patrones**: DDD Tactical (Entity, Value Object, Domain Event), Idempotent Writes, Distributed Lock si needed
- **Diccionario de Dominio**: `urgency`, `state`, `available`, `assigned`

---

**Próximo paso**: Cambiar `status: DRAFT` a `status: APPROVED` y disparar `/implement-backend` + `/implement-frontend` en paralelo.
