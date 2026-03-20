package com.maestria.qa.questions;

import net.serenitybdd.screenplay.Question;
import net.serenitybdd.screenplay.rest.questions.LastResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ResponseStatus - Question que valida el código HTTP de la última respuesta
 * 
 * Ejemplo de uso: actor.should(seeThat(ResponseStatus.code(), is(201)))
 */
public class ResponseStatus implements Question<Integer> {
    
    private static final Logger logger = LoggerFactory.getLogger(ResponseStatus.class);
    
    @Override
    public Integer answeredBy(net.serenitybdd.screenplay.Actor actor) {
        int statusCode = LastResponse.received().getStatusCode();
        logger.info("Response status code: {}", statusCode);
        return statusCode;
    }
    
    public static ResponseStatus code() {
        return new ResponseStatus();
    }
}
