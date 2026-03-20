package com.maestria.qa.questions;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maestria.qa.utils.RestContext;
import com.maestria.qa.utils.TestConstants;

import net.serenitybdd.screenplay.Question;

public class TurnosList implements Question<Integer> {

    private static final Logger logger = LoggerFactory.getLogger(TurnosList.class);

    @Override
    public Integer answeredBy(net.serenitybdd.screenplay.Actor actor) {
        List<?> turnos = RestContext.getLastResponse().jsonPath().getList(TestConstants.Payload.ROOT_LIST);
        int size = turnos.size();
        logger.info("Number of turnos in response: {}", size);
        return size;
    }

    public static TurnosList size() {
        return new TurnosList();
    }
}
