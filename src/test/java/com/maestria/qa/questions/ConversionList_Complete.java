package com.maestria.qa.questions;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.rest.questions.LastResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * ConversionList - Question que valida el tamaño y contenido de la lista de conversiones
 * 
 * Ejemplo de uso:
 *   int count = actor.asksFor(ConversionList.size());
 *   actor.should(seeThat(ConversionList.size(), greaterThan(0)));
 */
public class ConversionList implements Question<Integer> {
    
    private static final Logger logger = LoggerFactory.getLogger(ConversionList.class);
    
    @Override
    public Integer answeredBy(net.serenitybdd.screenplay.Actor actor) {
        List<?> conversions = LastResponse.received().jsonPath().getList("$");
        int size = conversions.size();
        logger.info("Number of conversions in response: {}", size);
        return size;
    }
    
    /**
     * Factory method para obtener el tamaño de la lista
     * 
     * @return Question que retorna el número de conversiones
     */
    public static ConversionList size() {
        return new ConversionList();
    }
}
