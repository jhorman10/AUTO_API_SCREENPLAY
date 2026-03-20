package com.maestria.qa.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Delete;
import net.serenitybdd.core.steps.Instrumented;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DeleteConversion - Tarea para eliminar una conversión vía DELETE /api/v1/conversions/{id}
 * 
 * Responsabilidad única: Enviar una petición DELETE para eliminar una conversión específica.
 */
public class DeleteConversion implements Task {
    
    private static final Logger logger = LoggerFactory.getLogger(DeleteConversion.class);
    
    private final String conversionId;
    
    public DeleteConversion(String conversionId) {
        this.conversionId = conversionId;
    }
    
    @Override
    public <T extends Actor> void performAs(T actor) {
        logger.info("Deleting conversion with ID: {}", conversionId);
        
        actor.attemptsTo(
            Delete.from("/conversions/" + conversionId)
        );
        
        logger.info("DELETE /conversions/{} executed", conversionId);
    }
    
    /**
     * Factory method para crear una tarea de DeleteConversion
     */
    public static DeleteConversion withId(String id) {
        return Instrumented.instanceOf(DeleteConversion.class)
            .withProperties(id);
    }
    
    public DeleteConversion withProperties(String id) {
        return this;
    }
}
