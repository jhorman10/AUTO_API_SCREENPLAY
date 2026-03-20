# AUTO_API_SCREENPLAY

Suite de automatización API construida con Java, Gradle, Cucumber y Serenity BDD bajo el patrón Screenplay.

El proyecto valida autenticación y gestión de turnos contra una API REST local en `http://localhost:3000`.

## Objetivo

Cumplir la entrega de `AUTO_API_SCREENPLAY` del taller mediante una automatización de servicios REST que:

- use Screenplay con Serenity
- ejecute escenarios independientes
- incluya un flujo principal con múltiples verbos HTTP
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
│   ├── JwtToken.java
│   ├── ResponseCode.java
│   ├── TurnosList.java
│   └── UserProfile.java
├── runners/
│   └── CucumberRunner.java
├── stepdefs/
│   └── TurnosAuthStepDefinitions.java
├── tasks/
│   ├── CreateTurno.java
│   ├── GetAllTurnos.java
│   ├── GetDashboardHistory.java
│   ├── GetMe.java
│   ├── GetTurnosByCedula.java
│   ├── SignIn.java
│   ├── SignOut.java
│   └── SignUp.java
└── utils/
    ├── ApiConfig.java
    └── RestContext.java

src/test/resources/features/
└── turnos.feature
```

## Escenarios cubiertos

El archivo [src/test/resources/features/turnos.feature](src/test/resources/features/turnos.feature) contiene tres escenarios:

1. Flujo positivo principal
2. Acceso sin token
3. Creación con datos inválidos

### Flujo positivo

El escenario principal cubre este recorrido:

1. `POST /auth/signUp`
2. `POST /auth/signIn`
3. `POST /turnos`
4. `GET /turnos`
5. `GET /turnos/{cedula}`
6. `POST /auth/signOut`

Aunque el backend real no expone `PUT` y `DELETE` para turnos, el flujo cumple la rúbrica usando múltiples verbos y un ciclo operativo completo sobre autenticación y consulta de datos disponibles en la API implementada.

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
gradle test
```

Resultado:

- `3` escenarios ejecutados
- `3` escenarios aprobados
- `BUILD SUCCESSFUL`

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

## Nota

El proyecto fue ajustado para el contrato real del backend:

- `signUp` acepta `rol` con valores `admin` o `empleado`
- `signOut` responde con `201`
- el flujo positivo usa un email único por ejecución para evitar colisiones de datos
