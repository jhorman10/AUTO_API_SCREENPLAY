package com.maestria.qa.questions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maestria.qa.utils.RestContext;
import com.maestria.qa.utils.TestConstants;

import net.serenitybdd.screenplay.Question;

public class JwtToken implements Question<String> {

    private static final Logger logger = LoggerFactory.getLogger(JwtToken.class);

    @Override
    public String answeredBy(net.serenitybdd.screenplay.Actor actor) {
        String token = RestContext.getLastResponse().jsonPath().get(TestConstants.Payload.TOKEN);
        logger.info("Extracted JWT token from response");
        return token;
    }

    public static JwtToken fromResponse() {
        return new JwtToken();
    }
}
