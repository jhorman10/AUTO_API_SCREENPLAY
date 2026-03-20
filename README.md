# AUTO_API_SCREENPLAY

Suite de automatización API construida con Java, Gradle, Cucumber y Serenity BDD bajo el patrón Screenplay.

El proyecto valida autenticación y gestión de turnos contra una API REST local en `http://localhost:3000`.

## Objetivo

Suite de automatización de servicios REST con patrón Screenplay que:

- use Screenplay con Serenity BDD
- ejecute escenarios independientes
- implemente 4 métodos HTTP (2 POST + 2 GET) con diferentes implementaciones
- genere reportes legibles
- mantenga código limpio y nombres semánticos

## Cumplimiento de la rúbrica

Este repositorio cubre el entregable `AUTO_API_SCREENPLAY` del taller.

- Patrón usado: Screenplay con Serenity Rest
- Lenguaje: Java
- Gestión de dependencias: Gradle
- Runner: Cucumber
- Reportería: Serenity BDD
- Escenarios independientes: sí
- Flujo positivo: sí
- Flujo negativo: sí
- Código comentado dentro de clases: no
- Nomenclatura semántica: sí

El escenario principal valida un flujo operativo completo sobre los endpoints realmente disponibles en la API desarrollada para el curso.

## Stack

- Java 11
- Gradle
- Serenity BDD 4.1.5
- Cucumber 7.14.0
- REST Assured 5.3.2
- JUnit 4

## Estructura

```text
src/test/java/com/maestria/qa/
├── actors/
│   └── ApiTester.java
├── questions/
│   ├── ErrorMessage.java
│   ├── ResponseCode.java
│   └── TurnosList.java
├── runners/
│   └── CucumberRunner.java
├── stepdefs/
│   └── TurnosAuthStepDefinitions.java
├── tasks/
│   ├── CreateTurno.java      (POST)
│   ├── GetAllTurnos.java     (GET)
│   ├── GetTurnosByCedula.java (GET)
│   └── SignUp.java           (POST)
└── utils/
    ├── ApiConfig.java
    └── RestContext.java

src/test/resources/features/
└── turnos.feature
```

## Escenarios cubiertos

El archivo [src/test/resources/features/turnos.feature](src/test/resources/features/turnos.feature) contiene dos escenarios:

1. **Complete Flow - Create and Retrieve Turnos** (Flujo positivo)
2. **Invalid Turno Creation** (Validación negativa)

### Métodos HTTP implementados

La suite utiliza exactamente **4 métodos HTTP** con diferentes implementaciones:

**POST (2 implementaciones):**
1. `POST /auth/signUp` — Registro de usuario
2. `POST /turnos` — Creación de turno

**GET (2 implementaciones):**
1. `GET /turnos` — Obtener listado completo de turnos
2. `GET /turnos/{cedula}` — Obtener turnos por cédula del paciente

### Flujo positivo

El escenario principal ejecuta:  
→ `POST /auth/signUp`  
→ `POST /turnos`  
→ `GET /turnos`  
→ `GET /turnos/{cedula}`

## Requisitos previos

- Java 11 instalado
- Gradle disponible en el sistema
- API backend ejecutándose en `http://localhost:3000`

## Configuración

La URL base está definida en [serenity.conf](serenity.conf):

```properties
api.base.url=http://localhost:3000
```

La inicialización HTTP se realiza en [src/test/java/com/maestria/qa/utils/ApiConfig.java](src/test/java/com/maestria/qa/utils/ApiConfig.java).

## Ejecución

Compilar pruebas:

```bash
gradle clean compileTestJava
```

Ejecutar toda la suite:

```bash
gradle test
```

## Resultado verificado

Última validación ejecutada:

```bash
gradle clean test
```

Resultado:

- `2` escenarios ejecutados
- `2` escenarios aprobados
- `BUILD SUCCESSFUL` en 5s

## Evidencia de ejecución

La ejecución exitosa puede revisarse en los reportes generados por Gradle y Serenity:

- [build/reports/tests/test/index.html](build/reports/tests/test/index.html)
- [target/site/serenity](target/site/serenity)

## Reportes

Los reportes quedan generados en:

- [build/reports/tests/test/index.html](build/reports/tests/test/index.html) — Reporte Gradle
- [target/cucumber-reports/cucumber.html](target/cucumber-reports/cucumber.html) — Reporte Cucumber
- [target/site/serenity/](target/site/serenity/) — Datos de Serenity (JSON)

### Abrir reporte de Gradle

macOS:

```bash
open build/reports/tests/test/index.html
```

Linux:

```bash
xdg-open build/reports/tests/test/index.html
```

### Abrir reporte de Cucumber

macOS:

```bash
open target/cucumber-reports/cucumber.html
```

Linux:

```bash
xdg-open target/cucumber-reports/cucumber.html
```

### Ver datos de Serenity

Los datos de Serenity están en formato JSON en `target/site/serenity/`:

```bash
ls target/site/serenity/
```

## Convenciones aplicadas

- Screenplay con separación entre Actor, Tasks, Questions y Step Definitions
- Tasks con responsabilidad única
- Nombres semánticos
- Sin archivos legacy de escenarios anteriores
- Integración con Serenity para reportería

## Comandos útiles

Ejecutar solo compilación:

```bash
gradle clean compileTestJava
```

Ejecutar solo el runner principal:

```bash
gradle test --tests com.maestria.qa.runners.CucumberRunner
```

## Métodos HTTP soportados

El proyecto utiliza solo **2 métodos HTTP** (POST y GET) con **4 implementaciones diferentes**:

| Método | Endpoint | Task | Descripción |
|--------|----------|------|-------------|
| POST | `/auth/signUp` | `SignUp.java` | Registro de usuario |
| POST | `/turnos` | `CreateTurno.java` | Creación de turno |
| GET | `/turnos` | `GetAllTurnos.java` | Listado de todos los turnos |
| GET | `/turnos/{cedula}` | `GetTurnosByCedula.java` | Turnos por cédula |

## Notas de implementación

- Arquitectura Screenplay con Actor → Tasks → Questions
- Cada Task es responsable de una operación HTTP específica
- Los datos de prueba incluyen email único por ejecución (timestamp)
- Los reportes se generan en `target/cucumber-reports/`
