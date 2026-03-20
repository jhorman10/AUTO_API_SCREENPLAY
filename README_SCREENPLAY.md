# AUTO_API_SCREENPLAY

Proyecto de **automatización de API REST** usando el patrón **Screenplay** con **Serenity BDD** en Java.

Este proyecto implementa un ciclo completo de pruebas CRUD (Create, Read, Update, Delete) contra una API REST, demostrando la aplicabilidad del patrón Screenplay para testing de servicios backend.

---

## 📋 Características

✅ **Patrón Screenplay** — Arquitectura escalable basada en Actores, Tareas, Acciones y Preguntas  
✅ **Ciclo CRUD Completo** — Validación de POST, GET, PUT, DELETE en una sola prueba  
✅ **Serenity BDD** — Reportes automáticos y trazabilidad de cada paso  
✅ **Cucumber/Gherkin** — Escenarios declarativos en español  
✅ **Gradle** — Gestión de dependencias centralizada  
✅ **Clean Code** — Responsabilidad única por clase, nomenclatura semántica  

---

## 🏗️ Estructura del Proyecto

```
src/test/
├── java/com/maestria/qa/
│   ├── actors/
│   │   └── ApiTester.java                    # Actor que realiza tareas contra la API
│   ├── tasks/
│   │   ├── CreateConversion.java             # POST /conversions
│   │   ├── FetchAllConversions.java          # GET /conversions
│   │   ├── FetchConversionById.java          # GET /conversions/{id}
│   │   ├── UpdateConversion.java             # PUT /conversions/{id}
│   │   └── DeleteConversion.java             # DELETE /conversions/{id}
│   ├── questions/
│   │   ├── ResponseStatus.java               # ¿Cuál fue el código HTTP?
│   │   ├── ConversionId.java                 # ¿Cuál fue el ID creado?
│   │   ├── ConversionStatus.java             # ¿Cuál es el estado?
│   │   └── ConversionList.java               # ¿Cuántos elementos hay?
│   ├── runners/
│   │   └── CucumberRunner.java               # Test runner configurado para Cucumber + Serenity
│   ├── stepdefs/
│   │   └── ConversionStepDefinitions.java    # Step definitions que conectan Gherkin con Tasks/Questions
│   └── utils/
│       ├── ApiConfig.java                    # Configuración centralizada de base URL, timeouts
│       └── ConversionDataFactory.java        # Factory para generar datos de prueba
└── resources/
    └── features/
        └── conversions.feature               # Escenarios Gherkin (en español)
```

---

## 🚀 Instalación y Configuración

### Requisitos
- **Java 11+**
- **Gradle 7.0+**
- **Git**

### Pasos

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/<tu-usuario>/AUTO_API_SCREENPLAY.git
   cd AUTO_API_SCREENPLAY
   ```

2. **Verificar que Gradle esté disponible**
   ```bash
   gradle --version
   ```

3. **Compilar el proyecto**
   ```bash
   gradle clean build
   ```

---

## ⚙️ Configuración de la API

El archivo `serenity.conf` define la configuración de la API:

```properties
api.base.url=http://localhost:8000/api/v1
```

**Para cambiar el endpoint:**
- Edita `serenity.conf` y actualiza `api.base.url`
- O ejecuta con variable de entorno: `gradle test -Dapi.base.url=http://otra-api.com`

---

## ▶️ Ejecutar las Pruebas

### Ejecutar todos los escenarios
```bash
gradle test
```

### Ejecutar solo escenarios críticos (marcados con @critical)
```bash
gradle test -Dcucumber.filter.tags="@critical"
```

### Ejecutar solo escenarios smoke (marcados con @smoke)
```bash
gradle test -Dcucumber.filter.tags="@smoke"
```

### Ejecutar escenarios negativos
```bash
gradle test -Dcucumber.filter.tags="@negative"
```

### Ejecutar escenarios específicos por palabra clave
```bash
gradle test -Dcucumber.filter.name="Ciclo Completo"
```

### Ejecutar con más verbosidad
```bash
gradle test --info
```

---

## 📊 Generar Reportes

Serenity genera reportes automáticamente en cada ejecución:

### HTML Report
```bash
gradle test
# Luego: target/site/serenity/index.html
```

### Reporte de Cucumber JSON
```bash
# Ubicado en: target/cucumber-reports/cucumber.json
# Se puede integrar con jenkins, gitlab-ci, etc.
```

Para abrir el reporte HTML después de ejecutar los tests:
```bash
open target/site/serenity/index.html  # macOS
xdg-open target/site/serenity/index.html  # Linux
start target/site/serenity/index.html  # Windows
```

---

## 🎯 Escenarios Implementados

### Escenario 1: Ciclo Completo (CRUD)
**Tag:** `@smoke @critical`

Flujo:
1. **POST** — Crear una conversión con tipo "venta" y valor $150.50
2. **GET (All)** — Obtener la lista de conversiones
3. **GET (By ID)** — Obtener la conversión por su ID
4. **PUT** — Actualizar el estado a "completada"
5. **DELETE** — Eliminar la conversión

**Validaciones:**
- Código 201 en POST
- Código 200 en GET/PUT
- Código 204 en DELETE
- Los datos retornados coinciden con los enviados
- El estado se actualiza correctamente

### Escenario 2: Error de Validación (Negativo)
**Tag:** `@negative @validation`

**Caso:** Intentar crear conversión sin `user_id` (campo obligatorio)

**Validaciones:**
- Código 422 (Unprocessable Entity)
- Mensaje de error menciona el campo faltante

---

## 🔍 Estructura de Tasks y Questions

### ¿Cómo agregar nuevas Tareas?

Las Tareas (Tasks) representan acciones que el actor realiza:

```java
public class MyNewTask implements Task {
    
    private final String parameter;
    
    public MyNewTask(String parameter) {
        this.parameter = parameter;
    }
    
    @Override
    public <T extends Actor> void performAs(T actor) {
        // Ejecutar la lógica de la tarea
        actor.attemptsTo(
            Post.to("/endpoint")
                .with(request -> request
                    .contentType("application/json")
                    .body(someData)
                )
        );
    }
    
    public static MyNewTask withParameter(String param) {
        return Instrumented.instanceOf(MyNewTask.class, param);
    }
}
```

### ¿Cómo agregar nuevas Preguntas?

Las Preguntas (Questions) validan información de la respuesta:

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

### ¿Cómo agregar nuevos Step Definitions?

Los Steps conectan el Gherkin con las Tasks y Questions:

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

---

## 🧪 Buenas Prácticas

### ✅ Sí:
- **Una responsabilidad por clase** — Cada Task hace UNA cosa
- **Nombres descriptivos** — `CreateConversion`, no `DoIt`
- **Factory methods** — `MyTask.withId(...)` en lugar de `new MyTask(...)`
- **Logging** — Cada paso queda registrado
- **Variables claras** — `currentConversionId`, no `id` o `x`

### ❌ No:
- ❌ Código comentado
- ❌ Variables de una letra (`x`, `y`, `id` sin contexto)
- ❌ Tasks que hacen múltiples cosas
- ❌ Step definitions con lógica compleja
- ❌ Magic numbers o strings sin explicación

---

## 🔧 Solución de Problemas

### ¿Mi test falla con "Connection refused"?
**Solución:** Verifica que la API esté corriendo en la URL configurada en `serenity.conf`

### ¿Los reportes no se generan?
**Solución:** Asegúrate de que `target/` existe; Gradle lo crea automáticamente

### ¿Las pruebas tardan mucho?
**Solución:** Aumenta el timeout en `serenity.conf` o reduce el número de intentos

### ¿Veo errores de "No such property"?
**Solución:** Verifica que los JSONPath en las Questions coincidan con la estructura real de respuesta

---

## 📚 Referencias

- [Serenity BDD Docs](https://serenity-bdd.info/)
- [Screenplay Pattern](https://serenity-bdd.info/docs/screenplay/screenplay_pattern)
- [Cucumber/Gherkin](https://cucumber.io/docs/gherkin/)
- [REST Assured](https://rest-assured.io/)

---

## 👨‍💼 Autor

**Maestría en Automatización** — Semana 5: Patrón Screenplay y Serenity BDD

---

## 📄 Licencia

MIT License — Libre para uso educativo y comercial
