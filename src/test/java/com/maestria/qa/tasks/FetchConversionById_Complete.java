package com.maestria.qa.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.core.steps.Instrumented;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FetchConversionById - Tarea que ejecuta GET /api/v1/conversions/{id}
 * 
 * Responsabilidad única: Enviar una petición GET para obtener una conversión
 * específica por su ID y capturar la respuesta.
 */
public class FetchConversionById implements Task {
    
    private static final Logger logger = LoggerFactory.getLogger(FetchConversionById.class);
    
    private final String conversionId;
    
    public FetchConversionById(String conversionId) {
        this.conversionId = conversionId;
    }
    
    @Override
    public <T extends Actor> void performAs(T actor) {
        logger.info("Fetching conversion by ID: {}", conversionId);
        
        actor.attemptsTo(
            Get.resource("/conversions/" + conversionId)
        );
        
        logger.info("GET /conversions/{} executed successfully", conversionId);
    }
    
    /**
     * Factory method para crear una instancia de FetchConversionById
     * 
     * @param id El ID de la conversión a obtener
     * @return Instancia instrumentada de FetchConversionById
     */
    public static FetchConversionById withId(String id) {
        return Instrumented.instanceOf(FetchConversionById.class, id);
    }
}
