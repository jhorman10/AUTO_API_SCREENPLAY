# Requerimiento: Sistema Inteligente de Asignación de Turnos Médicos

## Iniciativa
Sistema inteligente de gestión de turnos médicos con asignación de médicos por urgencia, disponibilidad real y comunicación en tiempo real.

## Problema Actual
- Pacientes enviados a consultorios vacíos
- Tiempos de espera indefinidos
- Falta de atención prioritaria a casos urgentes
- Invisibilidad operativa de disponibilidad de médicos

## Solución Propuesta
1. Registrar turno con nivel de urgencia (Alta, Media, Baja)
2. Visualizar posición en cola en tiempo real con reconexión automática
3. Notificación inmediata (<2s) cuando el turno se asigna a un médico disponible
4. Motor de asignación que respeta urgencia + FIFO + disponibilidad real

## Objetivos de Negocio
1. **Atención Garantizada**: 100% de asignaciones a médicos físicamente disponibles
2. **Priorización Clínica**: Alta > Media > Baja; FIFO dentro de la misma urgencia
3. **Lista de Espera Confiable**: Persistencia de posición aunque se desconecte
4. **Comunicación Real-time**: Notificación al paciente en <2 segundos post-asignación
5. **Operación Auditable**: Historial completo de decisiones de asignación

## Límites Definidos
- No rediseño visual completo
- No diagnóstico automático
- No gestión de HR (vacaciones, nóminas)
- No selección de médico por parte del paciente
- Urgencia definida por proceso operativo actual

## Historias de Usuario

### HU-01: Registro de Urgencia
**Como** recepcionista, **quiero** registrar un paciente con nivel de urgencia (Alta, Media, Baja), **para** que el sistema priorice la atención según criticidad clínica.

**Prioridad**: Media  
**Estimación**: 3 puntos  
**Dependencias**: HT-01, HT-02

### HU-02: Visualización de Posición
**Como** usuario en sala de espera, **quiero** ver en tiempo real mi posición en la cola, **para** conocer mi progreso sin preguntar en recepción.

**Prioridad**: Media  
**Estimación**: 8 puntos  
**Dependencias**: HT-02, HT-03

### HU-03: Notificación de Asignación de Médico
**Como** paciente, **quiero** recibir una notificación inmediata en la pantalla cuando mi turno sea asignado, **para** confirmar que seré atendido y saber a qué consultorio dirigirme.

**Prioridad**: Alta  
**Estimación**: 13 puntos  
**Dependencias**: HT-04, HT-05, HT-06

## Historias Técnicas Habilitadoras

### HT-01: Persistencia y Validación de Urgencia
Persistir turnos con nivel de urgencia validado en dominio e infraestructura.

**Estimación**: 3 puntos  
**Habilita**: HU-01

### HT-02: Proyección de Cola Consultable
Consulta de cola ordenada por prioridad + FIFO con tiempo de respuesta para real-time.

**Estimación**: 5 puntos  
**Habilita**: HU-02

### HT-03: Canal WebSocket con Reconexión
WebSocket resiliente para publicar cambios de posición sin refrescar.

**Estimación**: 8 puntos  
**Habilita**: HU-02

### HT-04: Motor de Asignación por Urgencia y Disponibilidad
Proceso que selecciona siguiente turno por urgencia + FIFO, valida disponibilidad de médico.

**Estimación**: 8 puntos  
**Habilita**: HU-03

### HT-05: Publicación Confiable de Evento de Asignación
AppointmentAssigned con reintentos y DLQ para entrega al menos una vez.

**Estimación**: 13 puntos  
**Habilita**: HU-03

### HT-06: Notificación Visible y Auditable
Consumo de evento + broadcast WebSocket + registro de auditoría con latencia <2s.

**Estimación**: 8 puntos  
**Habilita**: HU-03

## Criterios de Aceptación Generales

### Validación de Urgencia
- Solo valores permitidos: Alta, Media, Baja
- Registro sin urgencia válida se rechaza con HTTP 400
- Turno se persiste con estado inicial `waiting`

### Proyección de Cola
- Devuelve posición actual y estado de turno
- Ordenado por priority (Alta > Media > Baja) + created_at (ascendente)
- Respuesta sin datos innecesarios del dominio
- Tiempos <500ms para refresco real-time

### Asignación de Médico
- Respeta urgencia: Alta > Media > Baja
- En misma urgencia: FIFO por created_at
- Solo médicos en estado `available`
- Turno permanece en `waiting` si no hay médico disponible

### Notificación

- Máximo 2 segundos tras asignación en condiciones normales
- Muestra: nombre médico, consultorio, hora estimada
- Si no hay asignación, no muestra notificación
- Registro de auditoría con timestamp

## Distribución Sprint

**Sprint 1** (16 puntos): HT-01, HT-02, HT-03 — Base de datos + API de consulta + WebSocket  
**Sprint 2** (29 puntos): HT-04, HT-05, HT-06 — Motor inteligente + eventos + notificaciones

---

**Fecha Creación**: 2026-04-08  
**Estado**: Listo para Spec
