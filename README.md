# AUTO_API_SCREENPLAY

Suite de automatización de API REST construida con Java, Gradle, Cucumber y Serenity BDD bajo el patrón Screenplay.

El proyecto valida autenticación y gestión de turnos contra una API REST local en `http://localhost:3000`.

## Objetivo

Esta suite de automatización:

- usa Screenplay con Serenity BDD
- ejecuta escenarios independientes
- implementa 4 llamadas HTTP diferentes (2 POST + 2 GET)
- genera reportes legibles
- mantiene código limpio y nomenclatura semántica

## Características

- Patrón Screenplay con separación Actor, Tasks, Questions y Step Definitions
- Flujo positivo completo de registro y consulta de turnos
- Flujo negativo de validación
- Trazabilidad de ejecución con Serenity
- Escenarios declarativos en Gherkin

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

## Stack

- Java 11
- Gradle
- Serenity BDD 4.1.5
- Cucumber 7.14.0
- REST Assured 5.3.2
- JUnit 4

## Estructura del proyecto

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
│   ├── GetTurnosByCedula.java
│   └── SignUp.java
└── utils/
    ├── ApiConfig.java
    └── RestContext.java

src/test/resources/features/
└── turnos.feature
```

## Escenarios cubiertos

El archivo [src/test/resources/features/turnos.feature](src/test/resources/features/turnos.feature) contiene dos escenarios:

1. Complete Flow - Create and Retrieve Turnos
2. Invalid Turno Creation

### Métodos HTTP implementados

POST:
1. `POST /auth/signUp` - Registro de usuario
2. `POST /turnos` - Creación de turno

GET:
1. `GET /turnos` - Obtener listado de turnos
2. `GET /turnos/{cedula}` - Obtener turnos por cédula

### Flujo positivo

El escenario principal ejecuta:

1. `POST /auth/signUp`
2. `POST /turnos`
3. `GET /turnos`
4. `GET /turnos/{cedula}`

Con validaciones de código HTTP `202` para creación de turno y `200` para consultas.

### Flujo negativo

Se valida creación de turno con cédula inválida y respuesta esperada `400`.

## Requisitos previos

- Java 11 instalado
- Gradle disponible en el sistema
- API backend ejecutándose en `http://localhost:3000`

## Instalación y configuración

1. Clonar repositorio:

```bash
git clone https://github.com/<tu-usuario>/AUTO_API_SCREENPLAY.git
cd AUTO_API_SCREENPLAY
```

2. Verificar Gradle:

```bash
gradle --version
```

3. Compilar pruebas:

```bash
gradle clean compileTestJava
```

## Configuración de la API

La URL base está definida en [serenity.conf](serenity.conf):

```properties
api.base.url=http://localhost:3000
```

También puede sobreescribirse al ejecutar:

```bash
gradle test -Dapi.base.url=http://otra-api.com
```

La inicialización HTTP se realiza en [src/test/java/com/maestria/qa/utils/ApiConfig.java](src/test/java/com/maestria/qa/utils/ApiConfig.java).

## Ejecución de pruebas

Ejecutar toda la suite:

```bash
gradle test
```

Este comando ahora también genera automáticamente el reporte HTML de Serenity al finalizar la ejecución.

Ejecutar por tags:

```bash
gradle test -Dcucumber.filter.tags="@critical"
gradle test -Dcucumber.filter.tags="@smoke"
gradle test -Dcucumber.filter.tags="@negative"
```

Ejecutar por nombre de escenario:

```bash
gradle test -Dcucumber.filter.name="Complete Flow"
```

Ejecutar con verbosidad:

```bash
gradle test --info
```

Ejecutar solo el runner principal:

```bash
gradle test --tests com.maestria.qa.runners.CucumberRunner
```

## Resultado verificado

Última validación ejecutada:

```bash
gradle clean test
```

Resultado esperado:

- 2 escenarios ejecutados
- 2 escenarios aprobados
- BUILD SUCCESSFUL

## Reportes

Los reportes se generan en:

- [build/reports/tests/test/index.html](build/reports/tests/test/index.html) - Reporte Gradle
- [target/cucumber-reports/cucumber.html](target/cucumber-reports/cucumber.html) - Reporte Cucumber
- [target/cucumber-reports/cucumber.json](target/cucumber-reports/cucumber.json) - Resultado JSON para integración CI
- [target/site/serenity/index.html](target/site/serenity/index.html) - Reporte HTML interactivo de Serenity
- [target/site/serenity](target/site/serenity) - Evidencia base de Serenity (HTML/JSON/XML)

Abrir reporte de Gradle en Linux:

```bash
xdg-open build/reports/tests/test/index.html
```

Abrir reporte de Cucumber en Linux:

```bash
xdg-open target/cucumber-reports/cucumber.html
```

Abrir reporte de Serenity en Linux:

```bash
xdg-open target/site/serenity/index.html
```

Regenerar solo el HTML de Serenity usando resultados existentes:

```bash
gradle generateSerenityReport
```

Listar artefactos de Serenity en Linux:

```bash
ls target/site/serenity/
```

## Cómo extender la suite

### Agregar una Task

```java
public class MyNewTask implements Task {

    private final String parameter;

    public MyNewTask(String parameter) {
        this.parameter = parameter;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
            Post.to("/endpoint")
                .with(request -> request
                    .contentType("application/json")
                    .body(parameter)
                )
        );
    }

    public static MyNewTask withParameter(String param) {
        return Instrumented.instanceOf(MyNewTask.class, param);
    }
}
```

### Agregar una Question

```java
public class MyQuestion implements Question<String> {

    @Override
    public String answeredBy(Actor actor) {
        return LastResponse.received().jsonPath().get("field");
    }

    public static MyQuestion fromResponse() {
        return new MyQuestion();
    }
}
```

### Agregar Step Definitions

```java
@Cuando("ejecuta mi nueva tarea con {string}")
public void executesMyTask(String parameter) {
    actor.attemptsTo(MyNewTask.withParameter(parameter));
}

@Entonces("la pregunta retorna {string}")
public void verifyMyQuestion(String expected) {
    actor.should(seeThat(MyQuestion.fromResponse(), is(expected)));
}
```

## Buenas prácticas

Recomendado:

- Una responsabilidad por clase
- Nombres descriptivos
- Factory methods para creación de tasks
- Logging trazable por paso
- Variables con contexto de dominio

Evitar:

- Variables de una letra sin contexto
- Tasks con múltiples responsabilidades
- Step definitions con lógica compleja
- Valores mágicos sin explicación

## Solución de problemas

Si falla con `Connection refused`:
- Verificar que la API esté corriendo en la URL de [serenity.conf](serenity.conf).

Si no se generan reportes:
- Ejecutar `gradle test` y validar que existan las carpetas `build/` y `target/`.

Si hay errores de JSONPath:
- Revisar las Questions en [src/test/java/com/maestria/qa/questions](src/test/java/com/maestria/qa/questions) para alinear paths con la respuesta real.

## Referencias

- [Serenity BDD Docs](https://serenity-bdd.info/)
- [Screenplay Pattern](https://serenity-bdd.info/docs/screenplay/screenplay_pattern)
- [Cucumber/Gherkin](https://cucumber.io/docs/gherkin/)
- [REST Assured](https://rest-assured.io/)

## Licencia

MIT License
