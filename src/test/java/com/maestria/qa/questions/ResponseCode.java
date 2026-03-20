package com.maestria.qa.questions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maestria.qa.utils.RestContext;

import net.serenitybdd.screenplay.Question;

public class ResponseCode implements Question<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(ResponseCode.class);

    @Override
    public Integer answeredBy(net.serenitybdd.screenplay.Actor actor) {
        int code = RestContext.getLastResponse().getStatusCode();
        logger.info("Response status code: {}", code);
        return code;
    }

    public static ResponseCode fromLastResponse() {
        return new ResponseCode();
    }
}
