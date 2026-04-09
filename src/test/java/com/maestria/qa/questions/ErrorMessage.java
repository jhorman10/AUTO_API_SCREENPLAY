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
        Object rawMessage = RestContext.getLastResponse().jsonPath().get(TestConstants.Payload.MESSAGE);
        String message;
        if (rawMessage instanceof java.util.List) {
            message = String.join(", ", (java.util.List<String>) rawMessage);
        } else {
            message = rawMessage != null ? rawMessage.toString() : "";
        }
        logger.info("Error message from response: {}", message);
        return message;
    }

    public static ErrorMessage fromResponse() {
        return new ErrorMessage();
    }
}
