package com.maestria.qa.tasks;

import com.google.gson.JsonObject;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.serenitybdd.core.steps.Instrumented;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CreateConversion - Tarea que ejecuta POST /api/v1/conversions
 * 
 * Responsabilidad única: Enviar una petición POST con datos de conversión válida
 * y capturar el ID de la respuesta para uso en tareas posteriores.
 */
public class CreateConversion implements Task {
    
    private static final Logger logger = LoggerFactory.getLogger(CreateConversion.class);
    
    private final JsonObject conversionData;
    
    public CreateConversion(JsonObject conversionData) {
        this.conversionData = conversionData;
    }
    
    @Override
    public <T extends Actor> void performAs(T actor) {
        logger.info("Creating conversion with data: {}", conversionData);
        
        actor.attemptsTo(
            Post.to("/conversions")
                .with(request -> request
                    .contentType("application/json")
                    .body(conversionData.toString())
                )
        );
        
        logger.info("POST /conversions executed successfully");
    }
    
    /**
     * Factory method para crear una instancia de CreateConversion
     * 
     * @param data El objeto JSON con los datos de la conversión
     * @return Instancia instrumentada de CreateConversion
     */
    public static CreateConversion withData(JsonObject data) {
        return Instrumented.instanceOf(CreateConversion.class, data);
    }
}
