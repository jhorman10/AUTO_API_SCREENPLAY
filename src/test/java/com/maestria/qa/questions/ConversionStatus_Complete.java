package com.maestria.qa.questions;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.rest.questions.LastResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ConversionStatus - Question que extrae el estado de una conversión desde la respuesta
 * 
 * Ejemplo de uso:
 *   String status = actor.asksFor(ConversionStatus.fromLastResponse());
 *   actor.should(seeThat(ConversionStatus.fromLastResponse(), is("completada")));
 */
public class ConversionStatus implements Question<String> {
    
    private static final Logger logger = LoggerFactory.getLogger(ConversionStatus.class);
    
    @Override
    public String answeredBy(net.serenitybdd.screenplay.Actor actor) {
        String status = LastResponse.received().jsonPath().get("estado");
        logger.info("Conversion status: {}", status);
        return status;
    }
    
    /**
     * Factory method para extraer el estado de la última respuesta
     * 
     * @return Question que retorna el estado de la conversión
     */
    public static ConversionStatus fromLastResponse() {
        return new ConversionStatus();
    }
}
