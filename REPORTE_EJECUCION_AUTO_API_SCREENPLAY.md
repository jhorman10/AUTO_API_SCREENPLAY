# Reporte de Ejecucion AUTO_API_SCREENPLAY

## Identificacion

- Proyecto: AUTO_API_SCREENPLAY
- Fecha de validacion: 20 de marzo de 2026
- Tipo de automatizacion: API Testing
- Patron: Screenplay
- Framework: Serenity BDD
- Runner: Cucumber
- Build tool: Gradle

## Objetivo validado

Ejecutar una suite automatizada contra la API local de autenticacion y turnos para comprobar:

- flujo principal positivo
- acceso no autorizado sin token
- validacion de datos invalidos

## Comando ejecutado

```bash
gradle test
```

## Resultado general

- Estado final: BUILD SUCCESSFUL
- Escenarios ejecutados: 3
- Escenarios aprobados: 3
- Escenarios fallidos: 0

## Escenarios ejecutados

### 1. Complete CRUD Cycle - POST, GET, PUT, DELETE

Estado: PASSED

Cobertura operativa:

- POST /auth/signUp
- POST /auth/signIn
- POST /turnos
- GET /turnos
- GET /turnos/{cedula}
- POST /auth/signOut

### 2. Unauthorized Access Without Token

Estado: PASSED

Validacion:

- respuesta 401 cuando se consulta perfil sin token

### 3. Invalid Turno Creation

Estado: PASSED

Validacion:

- respuesta 400 al crear turno con cedula invalida

## Evidencia tecnica

Archivos de evidencia generados:

- [build/reports/tests/test/index.html](build/reports/tests/test/index.html)
- [build/test-results/test/TEST-com.maestria.qa.runners.CucumberRunner.xml](build/test-results/test/TEST-com.maestria.qa.runners.CucumberRunner.xml)
- [target/site/serenity](target/site/serenity)
- [target/cucumber-reports/cucumber.html](target/cucumber-reports/cucumber.html)

## Configuracion usada

- Base URL: `http://localhost:3000`
- Java target: 11
- Serenity: 4.1.5
- Cucumber: 7.14.0
- REST Assured: 5.3.2

## Hallazgos relevantes durante la estabilizacion

Se corrigieron varios desajustes hasta llegar a una ejecucion estable:

1. Conflicto entre `SerenityObjectFactory` y `PicoFactory`
2. Runner incorrecto sin listeners de Serenity
3. `Stage` de Screenplay no inicializado
4. `RestAssured.baseURI` no inicializada antes de las requests
5. Payload de `signUp` con `rol` no permitido por el backend
6. `signOut` con status real `201`
7. Flujo positivo con email repetible no estable

## Conclusiones

La suite se encuentra funcional, repetible y alineada con el contrato real del backend. El proyecto queda apto para:

- demostracion en vivo
- entrega academica
- consulta de reportes
- extension futura de escenarios

## Recomendaciones para la presentacion

1. Tener la API levantada antes de la demo
2. Ejecutar `gradle test` antes de compartir pantalla
3. Mostrar primero el feature y luego el reporte Serenity
4. Explicar que la automatizacion se ajusto a los endpoints realmente disponibles del backend
