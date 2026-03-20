package com.maestria.qa.questions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maestria.qa.utils.RestContext;
import com.maestria.qa.utils.TestConstants;

import net.serenitybdd.screenplay.Question;

public class ErrorMessage implements Question<String> {

    private static final Logger logger = LoggerFactory.getLogger(ErrorMessage.class);

    @Override
    public String answeredBy(net.serenitybdd.screenplay.Actor actor) {
        String message = RestContext.getLastResponse().jsonPath().get(TestConstants.Payload.MESSAGE);
        logger.info("Error message from response: {}", message);
        return message;
    }

    public static ErrorMessage fromResponse() {
        return new ErrorMessage();
    }
}
