# ⭐ BUENAS PRÁCTICAS - Screenp lay + Serenity BDD

Guía de mejores prácticas para mantener el código limpio, mantenible y profesional.

---

## 1️⃣ Nomenclatura Semántica

### ✅ SÍ

```java
// Classes - PascalCase descriptivos
public class CreateConversion implements Task { }
public class ResponseStatus implements Question<Integer> { }
public class ApiTester { }

// Methods - camelCase con verbo
public void performAs(Actor actor) { }
public static CreateConversion withData(JsonObject data) { }
public Integer answeredBy(Actor actor) { }

// Variables - camelCase con sustantivo descriptivo
private String conversionId;
private JsonObject updateData;
private Actor apiTester;

// Constants - UPPER_SNAKE_CASE
private static final String BASE_URL = "http://localhost:8000";
private static final int TIMEOUT_MS = 5000;
private static final String[] CONVERSION_TYPES = { "venta", "descarga" };
```

### ❌ NO

```java
// Nombres genéricos
public class Task1 implements Task { }
public class Q1 implements Question<Integer> { }

// Abreviaturas sin contexto
private String id;  // ¿ID de qué?
private double val;  // Valor de qué?
private int cnt;  // Contador de qué?

// Nombres confusos
public void doIt(Actor a) { }  // ¿Qué hace?
public static CreateConversion create() { }  // Parámetros confusos
```

---

## 2️⃣ Responsabilidad Única

### ✅ SÍ - Una Task = Una Acción

```java
// ✅ BIEN: CreateConversion solo crea
public class CreateConversion implements Task {
    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
            Post.to("/conversions")
                .with(request -> request.body(data))
        );
    }
}

// ✅ BIEN: FetchConversionById solo obtiene por ID
public class FetchConversionById implements Task {
    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
            Get.resource("/conversions/" + id)
        );
    }
}

// ✅ BIEN: UpdateConversion solo actualiza
public class UpdateConversion implements Task {
    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
            Put.to("/conversions/" + id)
                .with(request -> request.body(updates))
        );
    }
}
```

### ❌ NO - Una Task = Múltiples Acciones

```java
// ❌ MAL: Demasiadas responsabilidades
public class ConversionWorkflow implements Task {
    @Override
    public <T extends Actor> void performAs(T actor) {
        // Crear
        actor.attemptsTo(Post.to("/conversions").with(...));
        
        // Obtener el lista
        actor.attemptsTo(Get.resource("/conversions"));
        
        // Actualizar
        actor.attemptsTo(Put.to("/conversions/" + id).with(...));
        
        // Eliminar
        actor.attemptsTo(Delete.from("/conversions/" + id));
    }
}
```

**Alternativa:** Si necesitas múltiples pasos, hazlo en el StepDefinition:
```java
@Cuando("ejecuta el flujo completo de conversión")
public void executesCompleteFlow() {
    actor.attemptsTo(CreateConversion.withData(data));
    actor.attemptsTo(FetchAllConversions.fromTheApi());
    actor.attemptsTo(UpdateConversion.withId(id).andData(updates));
}
```

---

## 3️⃣ Logging Estratégico

### ✅ SÍ

```java
public class CreateConversion implements Task {
    
    private static final Logger logger = LoggerFactory.getLogger(CreateConversion.class);
    
    @Override
    public <T extends Actor> void performAs(T actor) {
        // Inicio de tarea
        logger.info("Creating conversion with data: {}", conversionData);
        
        // Ejecutar
        actor.attemptsTo(
            Post.to("/conversions")
                .with(request -> request.body(conversionData.toString()))
        );
        
        // Fin de tarea
        logger.info("POST /conversions executed successfully");
    }
}

public class ResponseStatus implements Question<Integer> {
    
    private static final Logger logger = LoggerFactory.getLogger(ResponseStatus.class);
    
    @Override
    public Integer answeredBy(Actor actor) {
        int statusCode = LastResponse.received().getStatusCode();
        logger.info("Response status code: {}", statusCode);  // Resultado
        return statusCode;
    }
}
```

### ❌ NO

```java
// ❌ Sin logging
public <T extends Actor> void performAs(T actor) {
    actor.attemptsTo(Post.to("/conversions").with(...));
}

// ❌ Logging en exceso
logger.debug("About to create");
logger.debug("Creating now");
logger.debug("Created");

// ❌ Logging sin contexto
logger.info("Done");  // ¿Qué se hizo?
logger.error("Error: " + e);  // No captura stack trace
```

---

## 4️⃣ Factory Methods

### ✅ SÍ - Patrones consistentes

```java
// Task
public class CreateConversion implements Task {
    public static CreateConversion withData(JsonObject data) {
        return Instrumented.instanceOf(CreateConversion.class, data);
    }
}

// Question
public class ResponseStatus implements Question<Integer> {
    public static ResponseStatus code() {
        return new ResponseStatus();
    }
}

// Uso intuitivo
actor.attemptsTo(CreateConversion.withData(data));
actor.should(seeThat(ResponseStatus.code(), is(201)));
```

### ❌ NO

```java
// ❌ Constructores públicos directo
public class CreateConversion implements Task {
    // Sin factory method
}
actor.attemptsTo(new CreateConversion(data));

// ❌ Factory methods inconsistentes
public static CreateConversion create(JsonObject data) { }  // vs. withData()
public class ResponseStatus implements Question<Integer> {
    public static ResponseStatus check() { }  // vs. code()
}

// ❌ Sin Instrumented (pierde reporte)
return new CreateConversion(data);  // Serenity no captura esto
```

---

## 5️⃣ Zero Commented Code

### ✅ SÍ - Código Auto-documentado

```java
public class FetchConversionById implements Task {
    
    private final String conversionId;
    
    public FetchConversionById(String conversionId) {
        this.conversionId = conversionId;
    }
    
    @Override
    public <T extends Actor> void performAs(T actor) {
        logger.info("Fetching conversion by ID: {}", conversionId);
        actor.attemptsTo(Get.resource("/conversions/" + conversionId));
        logger.info("GET /conversions/{} executed", conversionId);
    }
    
    public static FetchConversionById withId(String id) {
        return Instrumented.instanceOf(FetchConversionById.class, id);
    }
}
```

### ❌ NO

```java
public class FetchConversionById implements Task {
    
    // Legacy code - remove if not used
    // private final String id;
    
    private final String conversionId;
    
    // TODO: Fix this later
    // TODO: Handle errors
    
    public FetchConversionById(String conversionId) {
        // Set the ID
        this.conversionId = conversionId;
    }
    
    @Override
    public <T extends Actor> void performAs(T actor) {
        // Get the conversion
        // First, log the ID
        logger.info("Getting conversion: " + conversionId);  // Bad string concat
        
        // Make the request
        // Do the GET
        actor.attemptsTo(Get.resource("/conversions/" + conversionId));
        
        // Log it
        // Done
        logger.info("Executed");
    }
}
```

---

## 6️⃣ Data Factories para Tests

### ✅ SÍ

```java
public class ConversionDataFactory {
    
    public static JsonObject generateValidConversionData() {
        JsonObject conversion = new JsonObject();
        conversion.addProperty("user_id", generateUserId());
        conversion.addProperty("tipo", getRandomType());
        conversion.addProperty("valor", generateRandomValue());
        conversion.addProperty("estado", "pendiente");
        return conversion;
    }
    
    public static JsonObject generateConversionWithTypeAndValue(String tipo, double valor) {
        JsonObject conversion = new JsonObject();
        conversion.addProperty("user_id", generateUserId());
        conversion.addProperty("tipo", tipo);
        conversion.addProperty("valor", valor);
        conversion.addProperty("estado", "pendiente");
        return conversion;
    }
}

// Uso
var data = ConversionDataFactory.generateConversionWithTypeAndValue("venta", 100.50);
actor.attemptsTo(CreateConversion.withData(data));
```

### ❌ NO - Datos hardcoded

```java
@Cuando("crea una conversión")
public void createsConversion() {
    // ❌ Datos hardcoded
    var data = new JsonObject();
    data.addProperty("user_id", "user_123");  // Siempre el mismo
    data.addProperty("tipo", "venta");  // No varía
    data.addProperty("valor", 100.0);  // Valor fijo
    
    actor.attemptsTo(CreateConversion.withData(data));
}
```

---

## 7️⃣ Assertions Claras y Explícitas

### ✅ SÍ

```java
// Explicito con matcher
actor.should(seeThat(ResponseStatus.code(), is(201)));
actor.should(seeThat(ConversionList.size(), greaterThan(0)));
actor.should(seeThat(ConversionStatus.fromLastResponse(), is("completada")));

// Con descripción en el Question
actor.should(
    seeThat("Expected 201 status code", ResponseStatus.code(), is(201))
);
```

### ❌ NO

```java
// Assertions sin estructura
assert statusCode == 201;  // No capaca en reportes

// Comparaciones implícitas
if (actualStatus.equals(expectedStatus)) {
    // ¿Pasa o falla?
}

// Validaciones vagas
String response = LastResponse.received().asString();
// Luego validar manualmente el JSON
```

---

## 8️⃣ Gherkin Declarativo (Comportamiento, no Implementación)

### ✅ SÍ - Enfocado en QQUÉ

```gherkin
Funcionalidad: CRUD de Conversiones
  
  Escenario: Ciclo completo de conversión
    Cuando crea una conversión con tipo "venta" y valor 150.50
    Entonces la conversión se crea exitosamente con status 201
    Y obtiene la lista de conversiones
    Y la lista contiene al menos 1 conversión
    Y obtiene la conversión por su id creado
    Y actualiza la conversión a estado "completada"
    Y elimina la conversión
    Y la eliminación es exitosa con status 204
```

### ❌ NO - Enfocado en CÓMO

```gherkin
# ❌ Demasiado técnico, muy acoplado a la implementación
Escenario: CRUD via REST
  Cuando hago un POST a /api/v1/conversions con {"user_id":"...", "tipo":"venta", ...}
  Entonces obtengo status 201 y un json con id
  Y hago un GET a /api/v1/conversions
  Y parseo el json y valido el tamaño del array
  Y hago un GET a /api/v1/conversions/{id}
  Y comparo los campos del json
  Y hago un PUT a /api/v1/conversions/{id} con {"estado":"completada"}
  Y hago un DELETE a /api/v1/conversions/{id}
  Y el status es 204
```

---

## 9️⃣ Manejo de Datos Sensibles

### ✅ SÍ

```java
// No loguear datos sensibles
logger.info("Creating user with email: [REDACTED]");  // ✅
logger.info("Token: [REDACTED]");  // ✅

// O usar variables de entorno
private static final String API_KEY = System.getenv("API_KEY");
```

### ❌ NO

```java
// Loguear passwords, tokens, emails
logger.info("Creating user with email: " + email);  // ❌
logger.info("Using token: " + apiToken);  // ❌
```

---

## 🔟 Independencia de Tests

### ✅ SÍ

```java
@Escenario1_CrearConversion
public void createsConversion() {
    // INDEPENDIENTE: crea sus propios datos
    actor.attemptsTo(CreateConversion.withData(
        ConversionDataFactory.generateValidConversionData()
    ));
    // Valida
    actor.should(seeThat(ResponseStatus.code(), is(201)));
}

@Escenario2_ObtenerConversion
public void fetchesConversion() {
    // INDEPENDIENTE: crea su propia conversión primero
    actor.attemptsTo(CreateConversion.withData(
        ConversionDataFactory.generateValidConversionData()
    ));
    currentConversionId = actor.asksFor(ConversionId.fromLastResponse());
    
    // Luego la obtiene
    actor.attemptsTo(FetchConversionById.withId(currentConversionId));
    actor.should(seeThat(ResponseStatus.code(), is(200)));
}
```

### ❌ NO - Tests acoplados

```java
// ❌ Dependencia entre tests
@Escenario1_CrearConversion
public void createsConversion() {
    // Esto crea una conversión
    globalConversionId = crear();
}

@Escenario2_ObtenerConversion
public void fetchesConversion() {
    // Este DEPENDE del anterior
    // Si Escenario1 falla, este también falla
    usar(globalConversionId);
}
```

---

## Resumen - Checklist

Antes de hacer commit, verifica:

- [ ] ✅ Nombres descriptivos (PascalCase para clases, camelCase para variables)
- [ ] ✅ Una responsabilidad por clase
- [ ] ✅ Logging en inicio y fin de tareas
- [ ] ✅ Factory methods para Tasks y Questions
- [ ] ✅ Zero código comentado
- [ ] ✅ Datos de prueba desde Factories
- [ ] ✅ Assertions con matchers (not plain asserts)
- [ ] ✅ Gherkin enfocado en comportamiento
- [ ] ✅ Sin datos sensibles en logs
- [ ] ✅ Tests independientes entre sí

---

**Más info:** Consulta [ARQUITECTURA.md](./ARQUITECTURA.md) para patrones en detalle.
