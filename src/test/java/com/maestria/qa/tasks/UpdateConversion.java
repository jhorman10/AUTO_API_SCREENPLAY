package com.maestria.qa.tasks;

import com.google.gson.JsonObject;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Put;
import net.serenitybdd.core.steps.Instrumented;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UpdateConversion - Tarea para actualizar una conversión vía PUT /api/v1/conversions/{id}
 * 
 * Responsabilidad única: Enviar una petición PUT con los datos a actualizar.
 */
public class UpdateConversion implements Task {
    
    private static final Logger logger = LoggerFactory.getLogger(UpdateConversion.class);
    
    private final String conversionId;
    private final JsonObject updateData;
    
    public UpdateConversion(String conversionId, JsonObject updateData) {
        this.conversionId = conversionId;
        this.updateData = updateData;
    }
    
    @Override
    public <T extends Actor> void performAs(T actor) {
        logger.info("Updating conversion {} with data: {}", conversionId, updateData);
        
        actor.attemptsTo(
            Put.to("/conversions/" + conversionId)
                .with(request -> request
                    .contentType("application/json")
                    .body(updateData.toString())
                )
        );
        
        logger.info("PUT /conversions/{} executed", conversionId);
    }
    
    /**
     * Factory method para crear una tarea de UpdateConversion
     */
    public static UpdateConversion withId(String id) {
        return new UpdateConversion(id, new JsonObject());
    }
    
    public UpdateConversion andData(JsonObject data) {
        return new UpdateConversion(this.conversionId, data);
    }
}
