package com.maestria.qa.questions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maestria.qa.utils.RestContext;
import com.maestria.qa.utils.TestConstants;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class QueuePosition implements Question<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(QueuePosition.class);

    @Override
    public Integer answeredBy(Actor actor) {
        Integer position = RestContext.getLastResponse().jsonPath().get(TestConstants.Payload.POSITION);
        logger.info("Queue position from response: {}", position);
        return position;
    }

    public static QueuePosition fromLastResponse() {
        return new QueuePosition();
    }
}
