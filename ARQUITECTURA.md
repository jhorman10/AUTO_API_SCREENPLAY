# 🏗️ ARQUITECTURA - Patrón Screenplay + Serenity BDD

Guía detallada de la arquitectura implementada en AUTO_API_SCREENPLAY y cómo extenderla.

---

## 📐 Componentes Principales

```
┌─────────────────────────────────────────────────────────────┐
│                    FEATURE (Gherkin)                        │
│                   conversions.feature                       │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ↓
┌─────────────────────────────────────────────────────────────┐
│              STEP DEFINITIONS (Pasos)                       │
│            ConversionStepDefinitions.java                  │
│  @Cuando / @Entonces / @Dado                                │
└────────────────────────────┬────────────────────────────────┘
                             │
                ┌────────────┼────────────┐
                ↓            ↓            ↓
       ┌──────────────┐ ┌──────────┐ ┌───────────┐
       │    TASKS     │ │ QUESTIONS│ │   ACTOR   │
       │ (Acciones)   │ │(Validaci)│ │(Ejecuta)  │
       └──────────────┘ └──────────┘ └───────────┘
              │              │            │
         Crear    ────── Validar  ────── Hacer
       Conversión     Respuesta   Tareas
              │              │            │
              └──────────────┼────────────┘
                             ↓
                 ┌──────────────────────┐
                 │   REST INTERACTIONS  │
                 │ POST, GET, PUT, DELETE
                 └──────────────────────┘
                             │
                             ↓
                      ┌────────────────┐
                      │   API REST     │
                      │  /conversions  │
                      └────────────────┘
```

---

## 🎭 El Patrón Screenplay (Actores, Tareas, Preguntas)

### 1. **ACTOR** → El que realiza acciones

```java
// Crear el actor
Actor tester = ApiTester.withDefaultName();

// El actor ejecuta tareas
tester.attemptsTo(MyTask.withParameters(...));

// El actor responde preguntas
String value = tester.asksFor(MyQuestion.check(...));
```

**Archivo:** `actors/ApiTester.java`

---

### 2. **TASK** → Una acción clara y específica

Cada Task tiene UNA responsabilidad única.

```java
public class CreateConversion implements Task {
    
    private final JsonObject conversionData;
    
    @Override
    public <T extends Actor> void performAs(T actor) {
        // UNA SOLA ACCIÓN
        actor.attemptsTo(
            Post.to("/conversions")
                .with(request -> request
                    .body(conversionData.toString())
                )
        );
    }
    
    public static CreateConversion withData(JsonObject data) {
        return Instrumented.instanceOf(CreateConversion.class, data);
    }
}
```

**Archivos:** `tasks/CreateConversion.java`, `tasks/UpdateConversion.java`, etc.

**Principios:**
- Una responsabilidad por clase
- Factory method para instanciación
- Logging en cada paso
- Código sin comentarios

---

### 3. **QUESTION** → Una validación de estado

Cada Question responde una pregunta sobre la respuesta.

```java
public class ResponseStatus implements Question<Integer> {
    
    @Override
    public Integer answeredBy(Actor actor) {
        // Extraer información de la respuesta
        return LastResponse.received().getStatusCode();
    }
    
    public static ResponseStatus code() {
        return new ResponseStatus();
    }
}
```

**Archivos:** `questions/ResponseStatus.java`, `questions/ConversionId.java`, etc.

**Uso en assertions:**
```java
actor.should(seeThat(ResponseStatus.code(), is(201)));
```

---

### 4. **STEP DEFINITIONS** → Conectar Gherkin con Tasks/Questions

Los pasos en español se mapean a Tasks y Questions.

```java
@Cuando("crea una conversión con tipo {string} y valor {double}")
public void createsConversionWithTypeAndValue(String tipo, double valor) {
    var data = ConversionDataFactory.generateConversionWithTypeAndValue(tipo, valor);
    actor.attemptsTo(CreateConversion.withData(data));
}

@Entonces("la conversión se crea exitosamente con status {int}")
public void verifyConversionCreatedSuccessfully(int expectedStatus) {
    actor.should(seeThat(ResponseStatus.code(), is(expectedStatus)));
    currentConversionId = actor.asksFor(ConversionId.fromLastResponse());
}
```

**Archivo:** `stepdefs/ConversionStepDefinitions.java`

---

## 🔄 Flujo de Ejecución Completo

```
1. Feature se ejecuta (conversions.feature)
   ↓
2. Cada linea Gherkin dispara un Step Definition (@Cuando, @Entonces)
   ↓
3. El Step Definition instruye al Actor
   ↓
4. El Actor ejecuta una Task (actor.attemptsTo(...))
   ↓
5. La Task hace una llamada REST (POST, GET, PUT, DELETE)
   ↓
6. La API responde
   ↓
7. El Step Definition hace una Pregunta (actor.asksFor(...))
   ↓
8. La Question valida la respuesta
   ↓
9. La Assertion verifica que sea correcto (should seeThat(...))
   ↓
10. Serenity registra TODO en el reporte HTML
```

---

## 🛠️ Cómo Extender el Proyecto

### Agregar un nuevo endpoint (ej: GET /conversions/stats)

#### Paso 1: Crear una nueva Task

**stats/GetConversionStats.java**
```java
package com.maestria.qa.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.core.steps.Instrumented;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetConversionStats implements Task {
    
    private static final Logger logger = LoggerFactory.getLogger(GetConversionStats.class);
    
    @Override
    public <T extends Actor> void performAs(T actor) {
        logger.info("Fetching conversion statistics");
        
        actor.attemptsTo(
            Get.resource("/conversions/stats")
        );
        
        logger.info("GET /conversions/stats executed");
    }
    
    public static GetConversionStats fromApi() {
        return Instrumented.instanceOf(GetConversionStats.class);
    }
}
```

#### Paso 2: Crear una Question para validar

**ConversionStats.java**
```java
package com.maestria.qa.questions;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.rest.questions.LastResponse;

public class ConversionStats implements Question<Integer> {
    
    @Override
    public Integer answeredBy(net.serenitybdd.screenplay.Actor actor) {
        return LastResponse.received().jsonPath().get("total");
    }
    
    public static ConversionStats totalCount() {
        return new ConversionStats();
    }
}
```

#### Paso 3: Agregar un Step Definition

**ConversionStepDefinitions.java**
```java
@Cuando("obtiene las estadísticas de conversiones")
public void fetchesStats() {
    actor.attemptsTo(GetConversionStats.fromApi());
}

@Entonces("el total de conversiones es {int}")
public void verifyTotalCount(int expected) {
    actor.should(seeThat(ConversionStats.totalCount(), is(expected)));
}
```

#### Paso 4: Agregar a feature.feature

**conversions.feature**
```gherkin
Escenario: Obtener estadísticas de conversiones
  Cuando obtiene las estadísticas de conversiones
  Entonces el total de conversiones es 10
```

---

## 🏅 Mejores Prácticas

### ✅ SÍ — Code Limpio

```java
// Nombre descriptivo
public class FetchConversionById implements Task { }

// Responsabilidad única
@Override
public <T extends Actor> void performAs(T actor) {
    actor.attemptsTo(Get.resource("/conversions/" + id));
}

// Variable con contexto
private String conversionId;

// Logging estratégico
logger.info("Fetching conversion by ID: {}", conversionId);
```

### ❌ NO — Code Sucio

```java
// Nombre genérico
public class Task1 implements Task { }

// Múltiples responsabilidades
public void performAs(T actor) {
    actor.attemptsTo(Create.post(...));
    actor.attemptsTo(Get.resource(...));
    actor.attemptsTo(Delete.from(...));
}

// Variable sin contexto
private String id;

// Sin logging
// Código comentado aquí
```

---

## 📊 Ejemplo: Agregar un nuevo escenario completo

### Feature File
```gherkin
@smoke
Escenario: Actualizar múltiples conversiones en lote
  Cuando crea una conversión con tipo "venta" y valor 100.00
  Y almacena el ID para referencia
  Y crea otra conversión con tipo "descarga" y valor 50.00
  Y obtiene todas las conversiones
  Y verifica que hay al menos 2 conversiones
```

### Tasks
```java
// YaExiste CreateConversion
// YaExiste FetchAllConversions

// Nueva Task:
public class StoreConversionId implements Task {
    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.remember("lastConversionId", actor.asksFor(ConversionId.fromLastResponse()));
    }
    
    public static StoreConversionId forFutureUse() {
        return Instrumented.instanceOf(StoreConversionId.class);
    }
}
```

### Step Definitions
```java
@Y("almacena el ID para referencia")
public void storesConversionId() {
    actor.attemptsTo(StoreConversionId.forFutureUse());
}

@Y("obtiene todas las conversiones")
public void fetchesAll() {
    actor.attemptsTo(FetchAllConversions.fromTheApi());
}

@Y("verifica que hay al menos {int} conversiones")
public void verifiesCount(int expected) {
    actor.should(seeThat(ConversionList.size(), greaterThanOrEqualTo(expected)));
}
```

---

## 🧪 Estructura de Directorio (Recomendada)

```
src/test/java/com/maestria/qa/
├── actors/
│   └── ApiTester.java
├── tasks/
│   ├── crud/
│   │   ├── CreateConversion.java
│   │   ├── FetchAllConversions.java
│   │   ├── FetchConversionById.java
│   │   ├── UpdateConversion.java
│   │   └── DeleteConversion.java
│   └── reporting/
│       ├── GetStats.java
│       └── GetReports.java
├── questions/
│   ├── ResponseStatus.java
│   ├── ConversionId.java
│   ├── ConversionStatus.java
│   ├── ConversionList.java
│   └── TotalStats.java
├── runners/
│   └── CucumberRunner.java
├── stepdefs/
│   ├── ConversionStepDefinitions.java
│   └── ReportingStepDefinitions.java
└── utils/
    ├── ApiConfig.java
    ├── ConversionDataFactory.java
    └── ReportingDataFactory.java
```

---

## 🔗 Integración con Serenity

Serenity captura automáticamente:
- ✅ Cada paso ejecutado
- ✅ Datos enviados (Request body, headers)
- ✅ Datos recibidos (Response status, body)
- ✅ Tiempo de ejecución
- ✅ Logs anotados
- ✅ Pasadas/Fallidas
- ✅ Screenshots/Requests

El reporte HTML en `target/site/serenity/index.html` muestra todo esto de forma interactiva.

---

**Más info:** Consulta el [README_SCREENPLAY.md](./README_SCREENPLAY.md) para detalles de ejecución.
