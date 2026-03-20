package com.maestria.qa.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.core.steps.Instrumented;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FetchAllConversions - Tarea para obtener la lista de conversiones vía GET /api/v1/conversions
 * 
 * Responsabilidad única: Enviar una petición GET para obtener todas las conversiones.
 */
public class FetchAllConversions implements Task {
    
    private static final Logger logger = LoggerFactory.getLogger(FetchAllConversions.class);
    
    @Override
    public <T extends Actor> void performAs(T actor) {
        logger.info("Fetching all conversions");
        
        actor.attemptsTo(
            Get.resource("/conversions")
        );
        
        logger.info("GET /conversions executed");
    }
    
    /**
     * Factory method para crear una tarea de FetchAllConversions
     */
    public static FetchAllConversions fromTheApi() {
        return Instrumented.instanceOf(FetchAllConversions.class);
    }
}
