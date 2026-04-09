package com.maestria.qa.questions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maestria.qa.utils.RestContext;
import com.maestria.qa.utils.TestConstants;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class TurnoUrgency implements Question<String> {

    private static final Logger logger = LoggerFactory.getLogger(TurnoUrgency.class);

    @Override
    public String answeredBy(Actor actor) {
        String urgency = RestContext.getLastResponse().jsonPath().getString("[0]." + TestConstants.Payload.PRIORITY);
        logger.info("Appointment priority from response: {}", urgency);
        return urgency;
    }

    public static TurnoUrgency fromLastResponse() {
        return new TurnoUrgency();
    }
}
