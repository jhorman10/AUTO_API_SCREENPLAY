package com.maestria.qa.questions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maestria.qa.utils.RestContext;
import com.maestria.qa.utils.TestConstants;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class TurnoState implements Question<String> {

    private static final Logger logger = LoggerFactory.getLogger(TurnoState.class);

    @Override
    public String answeredBy(Actor actor) {
        String state = RestContext.getLastResponse().jsonPath().getString("[0]." + TestConstants.Payload.STATUS);
        logger.info("Appointment status from response: {}", state);
        return state;
    }

    public static TurnoState fromLastResponse() {
        return new TurnoState();
    }
}
