package com.maestria.qa.questions;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.rest.questions.LastResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ConversionId - Question que extrae el ID de la conversión creada desde la respuesta
 * 
 * Ejemplo de uso:
 *   String id = actor.asksFor(ConversionId.fromLastResponse());
 */
public class ConversionId implements Question<String> {
    
    private static final Logger logger = LoggerFactory.getLogger(ConversionId.class);
    
    @Override
    public String answeredBy(net.serenitybdd.screenplay.Actor actor) {
        String conversionId = LastResponse.received().jsonPath().get("uid");
        logger.info("Extracted conversion ID: {}", conversionId);
        return conversionId;
    }
    
    /**
     * Factory method para extraer el ID de la última respuesta
     * 
     * @return Question que retorna el ID de la conversión
     */
    public static ConversionId fromLastResponse() {
        return new ConversionId();
    }
}
