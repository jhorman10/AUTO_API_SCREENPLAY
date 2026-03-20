package com.maestria.qa.questions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maestria.qa.utils.RestContext;

import net.serenitybdd.screenplay.Question;

public class UserProfile implements Question<String> {

    private static final Logger logger = LoggerFactory.getLogger(UserProfile.class);

    private final String field;

    public UserProfile(String field) {
        this.field = field;
    }

    @Override
    public String answeredBy(net.serenitybdd.screenplay.Actor actor) {
        String value = RestContext.getLastResponse().jsonPath().get(field);
        logger.info("Extracted user field '{}': {}", field, value);
        return value;
    }

    public static UserProfile field(String fieldName) {
        return new UserProfile(fieldName);
    }

    public static UserProfile email() {
        return new UserProfile("email");
    }

    public static UserProfile nombre() {
        return new UserProfile("nombre");
    }

    public static UserProfile rol() {
        return new UserProfile("rol");
    }
}
