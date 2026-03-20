package com.maestria.qa.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.core.steps.Instrumented;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FetchAllConversions - Tarea que ejecuta GET /api/v1/conversions
 * 
 * Responsabilidad única: Enviar una petición GET para obtener todas las conversiones
 * y capturar la lista de respuesta.
 */
public class FetchAllConversions implements Task {
    
    private static final Logger logger = LoggerFactory.getLogger(FetchAllConversions.class);
    
    @Override
    public <T extends Actor> void performAs(T actor) {
        logger.info("Fetching all conversions from the API");
        
        actor.attemptsTo(
            Get.resource("/conversions")
        );
        
        logger.info("GET /conversions executed successfully");
    }
    
    /**
     * Factory method para crear una instancia de FetchAllConversions
     * 
     * @return Instancia instrumentada de FetchAllConversions
     */
    public static FetchAllConversions fromTheApi() {
        return Instrumented.instanceOf(FetchAllConversions.class);
    }
}
